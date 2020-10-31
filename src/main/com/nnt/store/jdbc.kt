package com.nnt.store

import com.alibaba.druid.pool.DruidDataSourceFactory
import com.nnt.core.Jsonobj
import com.nnt.core.logger
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import java.sql.Connection
import java.util.*
import javax.sql.DataSource

open class Jdbc : AbstractDbms() {

    var url: String = ""
    var user: String = ""
    var pwd: String = ""
    var driver: String = ""

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("url")) {
            logger.fatal("${id} 没有配置url")
            return false
        }
        url = cfg["url"].asText()

        if (!cfg.has("driver")) {
            logger.fatal("${id} 没有配置driver")
            return false
        }
        driver = cfg["driver"].asText()

        if (cfg.has("user"))
            user = cfg["user"].asText()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"].asText()

        return true
    }

    private lateinit var _dsfac: DataSource
    
    protected open fun propertiesForJdbc(): Properties {
        val props = Properties()
        props.setProperty("driverClassName", driver)
        props.setProperty("url", url)
        if (!user.isEmpty()) {
            props.setProperty("username", user)
            if (!pwd.isEmpty())
                props.setProperty("password", pwd)
        }

        // 设置连接数
        props.setProperty("initialSize", "0")
        props.setProperty("minIdle", "0")
        props.setProperty("maxActive", "512")

        return props
    }

    override fun open() {
        val props = propertiesForJdbc()
        _dsfac = DruidDataSourceFactory.createDataSource(props)
        logger.info("打开 ${id}@jdbc")
    }

    override fun close() {
        // pass
    }

    fun acquire(): JdbcSession {
        val conn = _dsfac.connection
        val tpl = JdbcTemplate(SingleConnectionDataSource(conn, true))
        return JdbcSession(conn, tpl)
    }

    // 直接执行sql语句返回原始数据类型
    fun execute(
        proc: (tpl: JdbcTemplate, conn: Connection) -> Unit
    ): Boolean {
        var r = true
        var conn: Connection? = null
        try {
            conn = _dsfac.connection
            val tpl = JdbcTemplate(SingleConnectionDataSource(conn, true))
            proc(tpl, conn)
        } catch (err: Throwable) {
            logger.exception(err.localizedMessage)
            r = false
        } finally {
            conn?.close()
        }
        return r
    }

}

// jdbc业务对象
open class JdbcSession(conn: Connection, tpl: JdbcTemplate) : JdbcOperations by tpl {

    private val _conn = conn
    private var _closed = false

    open fun close() {
        if (_closed) {
            _conn.close()
            _closed = true
        }
    }

    open fun commit() {
        // pass
    }

    protected fun finalize() {
        close()
    }
}