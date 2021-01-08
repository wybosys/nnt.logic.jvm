package com.nnt.store

import com.nnt.core.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.*
import org.springframework.jdbc.support.KeyHolder
import org.springframework.jdbc.support.rowset.SqlRowSet
import java.util.*
import kotlin.reflect.KClass

typealias JdbcKeepAliveAction = (ses: JdbcSession, tpl: JdbcTemplate) -> Unit

open class JdbcProperties : HikariConfig() {

    // 测试心跳的sql
    var keepAliveQuery: String? = "select 1"

    // 执行心跳的函数
    var keepAliveAction: JdbcKeepAliveAction? = null

    // 心跳间隔（毫秒）
    var keepAliveInterval: Long = 60000L
}

open class Jdbc : AbstractDbms() {

    var url: String = ""
    var user: String = ""
    var pwd: String = ""
    var driver: String = ""
    var slowquery: Long = DEFAULT_JDBC_SLOWQUERY

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

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

        if (cfg.has("user"))
            user = cfg["user"]!!.asString()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"]!!.asString()

        // json中按照秒配置
        if (cfg.has("slowquery"))
            slowquery = (cfg["slowquery"]!!.asDecimal() * 1000).toLong()

        return true
    }

    protected open fun propertiesForJdbc(): JdbcProperties {
        val props = DefaultJdbcProperties()
        props.driverClassName = driver
        props.jdbcUrl = url
        if (!user.isEmpty()) {
            props.username = user
            if (!pwd.isEmpty())
                props.password = pwd
        }
        return props
    }

    private var _ds: JdbcDataSource? = null

    override fun open() {
        val props = propertiesForJdbc()
        _ds = JdbcDataSource(props)
        logger.info("打开 ${id}@jdbc")
    }

    override fun close() {
        // pass
    }

    fun acquire(): JdbcSession {
        val ses = JdbcSession()
        ses.dataSource = _ds!!
        ses.slowquery = slowquery
        return ses
    }

    override fun acquireSession(): ISession {
        return acquire()
    }

    // 直接执行sql语句返回原始数据类型
    fun execute(
        proc: (tpl: JdbcTemplate) -> Unit,
    ): Boolean {
        var r = true
        try {
            proc(_ds!!.acquire())
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        }
        return r
    }

    companion object {

        // 默认jdbc配置
        fun DefaultJdbcProperties(): JdbcProperties {
            val props = JdbcProperties()
            props.poolName = "nnt.logic"
            props.minimumIdle = 0
            props.maximumPoolSize = 1024
            return props
        }
    }

}

open class JdbcDataSource(val properties: JdbcProperties) {

    // 数据源
    private var _ds: HikariDataSource? = null

    init {
        _ds = HikariDataSource(properties)
    }

    // 打开或者重新打开
    fun open() {
        close()
        _ds = HikariDataSource(properties)
    }

    // 关闭
    fun close() {
        if (_ds != null) {
            _ds!!.close()
        }
    }

    /**
     * 获取一个临时数据连接
     * 不需要关心释放，统一通过ds管理，注意需要避免长期拿在手中
     */
    fun acquire(): JdbcTemplate {
        return JdbcTemplate(_ds!!)
    }
}

// 5ms作为普通sql慢查询的默认阈值
val DEFAULT_JDBC_SLOWQUERY = 5L

// jdbc业务对象
open class JdbcSession : ISession {
    
    // 不使用 JdbcOperations by tpl 的写法是因为会造成编译器warnning
    private lateinit var _tpl: JdbcTemplate
    private var _ds: JdbcDataSource? = null

    var dataSource: JdbcDataSource
        get() {
            return _ds!!
        }
        set(value) {
            _ds = value
            _tpl = value.acquire()
        }

    // 记录日志使用的代号
    var logidr = "jdbc"

    override fun close() {
        stopKeepAlive()
    }

    override fun commit() {
        // pass
    }

    override fun rollback() {
        // pass
    }

    // 析构自动关闭
    protected fun finalize() {
        close()
    }

    // 开启长生命周期
    open fun immortal() {
        // 内部一般实现为自动心跳
        startKeepAlive()
    }

    protected var _tmr_keepalive: RepeatHandler? = null

    open fun startKeepAlive() {
        if (_tmr_keepalive != null) {
            logger.warn("${logidr} 已经启动keepalive")
            return
        }

        _tmr_keepalive = Repeat(_ds!!.properties.keepAliveInterval / 1000.0) {
            if (_ds!!.properties.keepAliveQuery != null) {
                _tpl.execute(_ds!!.properties.keepAliveQuery)
            }

            if (_ds!!.properties.keepAliveAction != null) {
                _ds!!.properties.keepAliveAction!!(this, _tpl)
            }
        }
    }

    open fun stopKeepAlive() {
        if (_tmr_keepalive != null) {
            CancelRepeat(_tmr_keepalive!!)
            _tmr_keepalive = null
        }
    }

    // 性能检测判断是否是慢查询的时间阈值，单位为毫秒
    var slowquery: Long = DEFAULT_JDBC_SLOWQUERY

    // 性能监测
    fun <T> metric(log: (cost: Long) -> Unit, proc: () -> T): T {
        val start = System.currentTimeMillis()
        val r = proc()
        val cost = System.currentTimeMillis() - start
        if (cost >= slowquery) {
            log(cost)
        }
        return r
    }

    // 代理

    open fun <T> query(sql: String, rse: ResultSetExtractor<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun query(sql: String, rch: RowCallbackHandler): Boolean = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            _tpl.query(sql, rch)
            return@metric true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric false
    }

    open fun <T> query(sql: String, rowMapper: RowMapper<T>): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun <T : Any> queryForList(sql: String, elementType: KClass<T>): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForList(sql, elementType.java)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun queryForList(sql: String): List<Map<String, Any>> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForList(sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun <T> query(sql: String, pss: PreparedStatementSetter, rse: ResultSetExtractor<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, pss, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T> query(sql: String, args: Array<Any>, argTypes: IntArray, rse: ResultSetExtractor<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, args, argTypes, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T> query(sql: String, args: Array<Any>, rse: ResultSetExtractor<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, args, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T> query(sql: String, rse: ResultSetExtractor<T>, vararg args: Any): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, rse, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun query(psc: PreparedStatementCreator, rch: RowCallbackHandler): Boolean {
        try {
            _tpl.query(psc, rch)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun query(sql: String, pss: PreparedStatementSetter, rch: RowCallbackHandler): Boolean = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            _tpl.query(sql, pss, rch)
            return@metric true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric false
    }

    open fun query(sql: String, args: Array<Any>, argTypes: IntArray, rch: RowCallbackHandler): Boolean = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            _tpl.query(sql, args, argTypes, rch)
            return@metric true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric false
    }

    open fun query(sql: String, args: Array<Any>, rch: RowCallbackHandler): Boolean = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            _tpl.query(sql, args, rch)
            return@metric true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric false
    }

    open fun query(sql: String, rch: RowCallbackHandler, vararg args: Any): Boolean = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            _tpl.query(sql, rch, *args)
            return@metric true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric false
    }

    open fun <T> query(psc: PreparedStatementCreator, rowMapper: RowMapper<T>): List<T> {
        try {
            return _tpl.query(psc, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T> query(sql: String, pss: PreparedStatementSetter, rowMapper: RowMapper<T>): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, pss, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun <T> query(sql: String, args: Array<Any>, argTypes: IntArray, rowMapper: RowMapper<T>): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, args, argTypes, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun <T> query(sql: String, args: Array<Any>, rowMapper: RowMapper<T>): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, args, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun <T> query(sql: String, rowMapper: RowMapper<T>, vararg args: Any): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.query(sql, rowMapper, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun <T> queryForObject(sql: String, rowMapper: RowMapper<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, rowMapper)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, requiredType.java)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T> queryForObject(sql: String, args: Array<Any>, argTypes: IntArray, rowMapper: RowMapper<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, args, argTypes, rowMapper)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T> queryForObject(sql: String, args: Array<Any>, rowMapper: RowMapper<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, args, rowMapper)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T> queryForObject(sql: String, rowMapper: RowMapper<T>, vararg args: Any): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, rowMapper, *args)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T : Any> queryForObject(sql: String, args: Array<Any>, argTypes: IntArray, requiredType: KClass<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, args, argTypes, requiredType.java)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T : Any> queryForObject(sql: String, args: Array<Any>, requiredType: KClass<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, args, requiredType.java)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>, vararg args: Any): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForObject(sql, requiredType.java, *args)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun queryForMap(sql: String): Map<String, Any>? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForMap(sql)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun queryForMap(sql: String, args: Array<Any>, argTypes: IntArray): Map<String, Any>? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForMap(sql, args, argTypes)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun queryForMap(sql: String, vararg args: Any): Map<String, Any>? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForMap(sql, *args)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun <T : Any> queryForList(sql: String, args: Array<Any>, elementType: KClass<T>): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            val ti = GetTableInfo(elementType)
            if (ti == null) {
                return@metric _tpl.queryForList(sql, toValue(args), elementType.java)
            }

            // 模型类型
            val r = _tpl.queryForList(sql, *toValue(args))
            return@metric r.map {
                val t = elementType.constructors.first().call()
                Fill(t, it, ti)
                t
            }
        } catch (err: Throwable) {
            // pass
        }
        return@metric listOf()
    }

    open fun <T : Any> queryForList(sql: String, elementType: KClass<T>, vararg args: Any): List<T> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            val ti = GetTableInfo(elementType)
            if (ti == null) {
                return@metric _tpl.queryForList(sql, elementType.java, *toValue(args))
            }

            // 模型类型
            val r = _tpl.queryForList(sql, *toValue(args))
            return@metric r.map {
                val t = elementType.constructors.first().call()
                Fill(t, it, ti)
                t
            }
        } catch (err: Throwable) {
            // pass
        }
        return@metric listOf()
    }

    open fun queryForList(sql: String, args: Array<Any>, argTypes: IntArray): List<Map<String, Any>> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForList(sql, args, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun queryForList(sql: String, vararg args: Any): List<Map<String, Any>> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForList(sql, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric listOf()
    }

    open fun queryForRowSet(sql: String): SqlRowSet? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForRowSet(sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun queryForRowSet(sql: String, args: Array<Any>, argTypes: IntArray): SqlRowSet? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForRowSet(sql, args, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun queryForRowSet(sql: String, vararg args: Any): SqlRowSet? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.queryForRowSet(sql, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric null
    }

    open fun update(sql: String): Int = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.update(sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric 0
    }

    open fun update(psc: PreparedStatementCreator): Int {
        try {
            return _tpl.update(psc)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun update(psc: PreparedStatementCreator, generatedKeyHolder: KeyHolder): Int {
        try {
            return _tpl.update(psc, generatedKeyHolder)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun update(sql: String, pss: PreparedStatementSetter): Int = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.update(sql, pss)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric 0
    }

    open fun update(sql: String, args: Array<Any>, argTypes: IntArray): Int = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.update(sql, args, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric 0
    }

    open fun update(sql: String, vararg args: Any?): Int = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.update(sql, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric 0
    }

    open fun batchUpdate(vararg sql: String): IntArray = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.batchUpdate(*sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric intArrayOf()
    }

    open fun batchUpdate(sql: String, pss: BatchPreparedStatementSetter): IntArray = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.batchUpdate(sql, pss)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric intArrayOf()
    }

    open fun batchUpdate(sql: String, batchArgs: List<Array<Any>>): IntArray = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.batchUpdate(sql, batchArgs)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric intArrayOf()
    }

    open fun batchUpdate(sql: String, batchArgs: List<Array<Any>>, argTypes: IntArray): IntArray = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.batchUpdate(sql, batchArgs, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric intArrayOf()
    }

    open fun <T> batchUpdate(
        sql: String,
        batchArgs: Collection<T>,
        batchSize: Int,
        pss: ParameterizedPreparedStatementSetter<T>,
    ): Array<IntArray> = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            return@metric _tpl.batchUpdate(sql, batchArgs, batchSize, pss)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric arrayOf()
    }

    open fun <T> execute(action: ConnectionCallback<T>): T? {
        try {
            return _tpl.execute(action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> execute(action: StatementCallback<T>): T? {
        try {
            return _tpl.execute(action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun execute(sql: String): Boolean = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        try {
            _tpl.execute(sql)
            return@metric true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return@metric false
    }

    open fun <T> execute(csc: CallableStatementCreator, action: CallableStatementCallback<T>): T? {
        try {
            return _tpl.execute(csc, action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> execute(callString: String, action: CallableStatementCallback<T>): T? {
        try {
            return _tpl.execute(callString, action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun call(csc: CallableStatementCreator, declaredParameters: List<SqlParameter>): Map<String, Any> {
        try {
            return _tpl.call(csc, declaredParameters)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return mapOf()
    }
}
