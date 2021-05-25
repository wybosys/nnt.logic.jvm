package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger

private const val DEFAULT_PORT = 9765

class Presto : Mybatis() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var catalog: String = ""
    var schema: String = "default"

    override fun config(cfg: JsonObject): Boolean {
        slowquery = DEFAULT_PRESTO_SLOWQUERY

        // mybatis需要外部绑定url，保护一下
        if (!cfg.has("url"))
            cfg["url"] = ""
        if (!cfg.has("driver"))
            cfg["driver"] = ""

        // mybatis初始化
        if (!super.config(cfg))
            return false

        // 默认
        if (user.isEmpty())
            user = "any"
        driver = "com.facebook.presto.jdbc.PrestoDriver"

        if (!cfg.has("host")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["host"]!!.asString()
        val sp = th.split(":")
        if (sp.size == 1) {
            host = th
        } else {
            host = sp[0]
            port = sp[1].toInt()
        }

        url = "jdbc:presto://${host}:${port}/"

        // 设置默认数据库
        if (cfg.has("schema"))
            schema = cfg["schema"]!!.asString()

        // 设置连接类型
        if (cfg.has("catalog")) {
            catalog = cfg["catalog"]!!.asString()
        } else {
            logger.fatal("${id} 没有配置数据库类型")
            return false
        }

        return true
    }

    override fun verify(): Boolean {
        return jdbc { ses ->
            // 执行测试脚本
            val cnt = ses.queryForObject(
                "select 1",
                Int::class
            )
            if (cnt != 1) {
                throw Error("presto 查询链接可用性失败")
            }
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
        val ses = PrestoJdbcSession(this)
        ses.dataSource = _dsfac
        ses.slowquery = slowquery
        return ses
    }

    override fun propertiesForJdbc(): JdbcProperties {
        val props = super.propertiesForJdbc()
        props.isReadOnly = true
        props.schema = schema
        props.catalog = catalog
        return props
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }

}

val DEFAULT_PRESTO_SLOWQUERY = 5000L

class PrestoJdbcSession(presto:Presto):JdbcSession() {

    private var _presto = presto

}