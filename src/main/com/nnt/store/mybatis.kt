package com.nnt.store

import com.nnt.core.File
import com.nnt.core.JsonObject
import com.nnt.core.URI
import com.nnt.core.logger
import com.nnt.store.reflect.TableInfo
import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.cursor.Cursor
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.*
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import kotlin.reflect.KClass

open class Mybatis : AbstractRdb() {

    var url: String = ""
    var user: String = ""
    var pwd: String = ""
    var driver: String = ""
    var maps = listOf<URI>()
    var slowquery = DEFAULT_JDBC_SLOWQUERY

    // 纯mybatis不支持获得数据库信息
    override fun tables(): Map<String, TableInfo> {
        return mutableMapOf()
    }

    override fun table(name: String): TableInfo? {
        return null
    }

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (cfg.has("user"))
            user = cfg["user"]!!.asString()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"]!!.asString()

        if (!cfg.has("url")) {
            logger.fatal("${id} 没有配置url")
            return false
        }
        url = cfg["url"]!!.asString()

        if (!cfg.has("driver")) {
            logger.fatal("${id} 没有配置driver")
            return false
        }
        driver = cfg["driver"]!!.asString()

        if (cfg.has("mybatis")) {
            val nmb = cfg["mybatis"]!!
            if (!nmb.has("map")) {
                logger.fatal("${id} 没有配置mybatis的map数据")
                return false
            }
            maps = nmb["map"]!!.map { URI(it.asString()) }
        }

        // json中按照秒配置
        if (cfg.has("slowquery"))
            slowquery = (cfg["slowquery"]!!.asDecimal() * 1000).toLong()

        return true
    }

    override fun open() {
        if (openJdbc()) {
            openMapper()
            logger.info("连接 ${id}@mybatis")
        } else {
            logger.info("连接 ${id}@mybatis 失败")
        }
    }

    // 验证是否打开
    open fun verify(): Boolean {
        return true
    }

    protected open fun propertiesForJdbc(): JdbcProperties {
        val props = Jdbc.DefaultJdbcProperties()
        props.jdbcUrl = url
        props.poolName = "nnt.logic"

        // 有些特殊情况下，不使用driverClassName设置jdbc的driver
        if (!driver.isEmpty())
            props.driverClassName = driver

        if (!user.isEmpty()) {
            props.username = user
            if (!pwd.isEmpty())
                props.password = pwd
        }

        return props
    }

    // 提取mybatis-mapper的对象
    protected lateinit var _mapfac: SqlSessionFactory

    // 数据源+连接池
    protected lateinit var _dsfac: JdbcDataSource

    protected fun openJdbc(): Boolean {
        val props = propertiesForJdbc()
        _dsfac = JdbcDataSource(props)
        return verify()
    }

    protected fun openMapper() {
        // 初始化数据源
        val fac = JdbcDataSourceFactory()
        val props = propertiesForJdbc()
        fac.setProperties(props)

        // 初始化环境
        val transfac = JdbcTransactionFactory()
        val env = Environment(id, transfac, fac.dataSource)

        // 初始化连接配置
        val conf = Configuration(env)

        // 遍历加入map
        maps.forEach() {
            try {
                val builder =
                    XMLMapperBuilder(
                        File(it).open(),
                        conf,
                        it.path,
                        conf.sqlFragments
                    )
                builder.parse()
            } catch (err: Throwable) {
                logger.exception(err)
            }
        }

        _mapfac = SqlSessionFactoryBuilder().build(conf)
    }

    override fun close() {
        _dsfac.close()
    }

    // 使用mybatis的mapper操作orm数据
    fun mapper(
        proc: (session: MybatisSession) -> Unit,
    ): Boolean {
        var r = true
        var ses: SqlSession? = null
        try {
            ses = _mapfac.openSession(false)
            proc(MybatisSession(ses))
            ses.commit()
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        } finally {
            ses?.close()
        }
        return r
    }

    // 直接执行sql语句返回原始数据类型
    open fun jdbc(
        proc: (ses: JdbcSession) -> Unit,
    ): Boolean {
        var r = true
        try {
            val ses = acquireJdbc()
            proc(ses)
            ses.close()
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        }
        return r
    }

    open fun acquireJdbc(): JdbcSession {
        val ses = JdbcSession()
        ses.dataSource = _dsfac
        return ses
    }

    open fun acquireSql(): MybatisSession {
        val ses = _mapfac.openSession(false)
        return MybatisSession(ses)
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }
}

// mybatis业务对象
open class MybatisSession(sql: SqlSession) : ISession {

    private val _sql = sql
    private var _closed = false

    override fun close() {
        if (!_closed) {
            _sql.close()
            _closed = true
        }
    }

    override fun commit() {
        _sql.commit()
    }

    override fun rollback() {
        _sql.rollback()
    }

    protected fun finalize() {
        close()
    }

    // 在mybatis框架中，在SqlSession未关闭之前，在一个session里面，如果执行相同的select语句，mybatis不会重新查询数据库，而是直接返回缓存在内存中的查询结果，这个是与MyBatis的Cache配置无关的，更改配置文件不起作用，要调用SqlSession.clearCache()函数才可以
    fun clearCache() {
        _sql.clearCache()
    }

    // 自动清除缓存，默认为false，保持和mybatis的通用规则
    var autoClearCache = false

    private inline fun <T> _select(proc: () -> T): T {
        if (autoClearCache)
            _sql.clearCache()
        return proc()
    }

    fun <T : Any> selectOne(statement: String): T? = _select {
        return _sql.selectOne(statement)
    }

    fun <T : Any> selectOne(statement: String, parameters: T): T? = _select {
        return _sql.selectOne(statement, parameters)
    }

    fun <T : Any> selectList(statement: String): List<T> = _select {
        return _sql.selectList(statement)
    }

    fun <T : Any> selectList(statement: String, parameters: T): List<T> = _select {
        return _sql.selectList(statement, parameters)
    }

    fun <T : Any> selectList(statement: String, parameters: T, rowbounds: RowBounds): List<T> = _select {
        return _sql.selectList(statement, parameters, rowbounds)
    }

    fun <K, V : Any> selectMap(statement: String, mapkey: String): Map<K, V> = _select {
        return _sql.selectMap(statement, mapkey)
    }

    fun <K, V : Any> selectMap(statement: String, parameters: V, mapkey: String): Map<K, V> = _select {
        return _sql.selectMap(statement, parameters, mapkey)
    }

    fun <K, V : Any> selectMap(statement: String, parameters: V, mapkey: String, rowbounds: RowBounds): Map<K, V> =
        _select {
            return _sql.selectMap(statement, parameters, mapkey, rowbounds)
        }

    fun <T> selectCursor(statement: String): Cursor<T> = _select {
        return _sql.selectCursor(statement)
    }

    fun <T : Any> selectCursor(statement: String, parameters: T): Cursor<T> = _select {
        return _sql.selectCursor(statement, parameters)
    }

    fun <T : Any> selectCursor(statement: String, parameters: T, rowbounds: RowBounds): Cursor<T> = _select {
        return _sql.selectCursor(statement, parameters, rowbounds)
    }

    fun <T> select(statement: String, proc: (ctx: ResultContext<out T>) -> Unit) = _select {
        _sql.select(statement, object : ResultHandler<T> {
            override fun handleResult(resultContext: ResultContext<out T>) {
                proc(resultContext)
            }
        })
    }

    fun <T : Any> select(statement: String, parameters: T, proc: (ctx: ResultContext<out T>) -> Unit) = _select {
        _sql.select(statement, parameters, object : ResultHandler<T> {
            override fun handleResult(resultContext: ResultContext<out T>) {
                proc(resultContext)
            }
        })
    }

    fun <T : Any> select(
        statement: String,
        parameters: T,
        rowbounds: RowBounds,
        proc: (ctx: ResultContext<out T>) -> Unit
    ) = _select {
        _sql.select(statement, parameters, rowbounds, object : ResultHandler<T> {
            override fun handleResult(resultContext: ResultContext<out T>) {
                proc(resultContext)
            }
        })
    }

    fun insert(statement: String): Int {
        return _sql.insert(statement)
    }

    fun <T : Any> insert(statement: String, parameters: T): Int {
        return _sql.insert(statement, parameters)
    }

    fun update(statement: String): Int {
        return _sql.update(statement)
    }

    fun <T : Any> update(statement: String, parameters: T): Int {
        return _sql.update(statement, parameters)
    }

    fun delete(statement: String): Int {
        return _sql.delete(statement)
    }

    fun <T : Any> delete(statement: String, parameters: T): Int {
        return _sql.delete(statement, parameters)
    }

    fun <T : Any> getMapper(clz: KClass<T>): T {
        return _sql.getMapper(clz.java)
    }
}