package com.nnt.store

import com.nnt.core.File
import com.nnt.core.JsonObject
import com.nnt.core.URI
import com.nnt.core.logger
import com.nnt.store.reflect.TableInfo
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*
import javax.sql.DataSource

open class Mybatis : AbstractRdb() {

    var url: String = ""
    var user: String = ""
    var pwd: String = ""
    var driver: String = ""
    var maps = listOf<URI>()

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

        return true
    }

    protected lateinit var _mapfac: SqlSessionFactory
    private lateinit var _dsfac: DataSource

    // 获得数据源
    open fun dataSource(): DataSource {
        return _dsfac
    }

    override fun open() {
        _dsfac = openJdbc()
        if (verify()) {
            _mapfac = openMapper()
            logger.info("连接 ${id}@mybatis")
        } else {
            logger.info("连接 ${id}@mybatis 失败")
        }
    }

    // 验证是否打开
    protected open fun verify(): Boolean {
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

    open fun openJdbc(): DataSource {
        val props = propertiesForJdbc()
        return HikariDataSource(props)
    }

    open fun openMapper(): SqlSessionFactory {
        // 初始化数据源
        val fac = PooledDataSourceFactory()
        val props = Properties()
        props.setProperty("driver", driver)
        props.setProperty("url", url)
        if (!user.isEmpty()) {
            props.setProperty("username", user)
            if (!pwd.isEmpty())
                props.setProperty("password", pwd)
        }
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

        return SqlSessionFactoryBuilder().build(conf)
    }

    override fun close() {
        // pass
    }

    // 使用mybatis的mapper操作orm数据
    fun mapper(
        proc: (session: SqlSession) -> Unit,
    ): Boolean {
        var r = true
        var ses: SqlSession? = null
        try {
            ses = _mapfac.openSession(false)
            proc(ses)
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
    fun jdbc(
        proc: (ses: JdbcSession) -> Unit,
    ): Boolean {
        var r = true
        try {
            val ses = JdbcSession(JdbcTemplate(_dsfac))
            proc(ses)
            ses.close()
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        }
        return r
    }

    open fun acquireJdbc(): JdbcSession {
        val tpl = JdbcTemplate(_dsfac)
        return JdbcSession(tpl)
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
class MybatisSession(sql: SqlSession) : SqlSession by sql {

    private val _sql = sql
    private var _closed = false

    override fun close() {
        if (!_closed) {
            _sql.close()
            _closed = true
        }
    }

    protected fun finalize() {
        close()
    }
}