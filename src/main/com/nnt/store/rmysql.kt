package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger
import com.nnt.store.reflect.TableInfo

private const val DEFAULT_PORT = 3306

class RMysql : Mybatis() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var schema: String = ""

    override fun config(cfg: JsonObject): Boolean {
        // mybatis需要外部绑定url，保护一下
        if (!cfg.has("url"))
            cfg["url"] = ""
        if (!cfg.has("driver"))
            cfg["driver"] = ""

        if (!super.config(cfg))
            return false

        if (!cfg.has("schema")) {
            logger.fatal("${id} 没有配置数据表")
            return false
        }
        schema = cfg["schema"]!!.asString()

        if (!cfg.has("host")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["host"]!!.asString()
        if (th.startsWith("unix://")) {
            logger.fatal("${id} java不支持使用管道连接")
            return false
        } else {
            val sp = th.split(":")
            if (sp.size == 1) {
                host = th
            } else {
                host = sp[0]
                port = sp[1].toInt()
            }
        }

        return true
    }

    override fun tables(): Map<String, TableInfo> {
        val r = mutableMapOf<String, TableInfo>()
        jdbc {
            val res = it.queryForList(
                "show tables"
            )
            res.forEach {
                val nm = it.values.first()
                val ti = TableInfo()
                ti.name = nm as String
                r[ti.name] = ti
            }
        }
        return r
    }

    override fun table(name: String): TableInfo? {
        var ti: TableInfo? = null
        jdbc {
            val res = it.queryForList(
                "describe ${name}"
            )

            if (res.size != 0) {
                ti = TableInfo()
                ti!!.name = name
            }
        }
        return ti
    }

    override fun open() {
        if (openJdbc()) {
            openMapper()
            logger.info("连接 ${id}@mysql")
        } else {
            logger.info("连接 ${id}@mysql 失败")
        }
    }

    override fun verify(): Boolean {
        return jdbc { ses ->
            val cnt = ses.queryForObject(
                "select 1",
                Int::class
            )
            if (cnt != 1)
                throw Error("mysql连接测试失败")
        }
    }

    override fun jdbc(
        proc: (ses: JdbcSession) -> Unit,
    ): Boolean {
        var r = true
        try {
            val ses = acquireSession() as JdbcSession
            proc(ses)
            ses.close()
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        }
        return r
    }

    override fun acquireJdbc(): JdbcSession {
        val ses = MysqlJdbcSession(this)
        ses.dataSource = _dsfac
        ses.slowquery = slowquery
        return ses
    }

    override fun propertiesForJdbc(): JdbcProperties {
        val props = super.propertiesForJdbc()
        props.poolName = "nnt.logic.mysql"
        props.driverClassName = "com.mysql.cj.jdbc.Driver"
        props.jdbcUrl = "jdbc:mysql://${host}:${port}/${schema}?characterEncoding=UTF-8"
        if (!user.isEmpty()) {
            props.username = user
            if (!pwd.isEmpty())
                props.password = pwd
        }
        props.schema = schema
        return props
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }

    override fun close() {
        _dsfac.close()
    }

}

class MysqlJdbcSession(mysql: RMysql) : JdbcSession() {

    private var _mysql = mysql

    init {
        logidr = "mysql"
    }
}