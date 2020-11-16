package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger
import org.neo4j.driver.*

private const val DEFAULT_PORT = 7687

open class Neo4J : AbstractGraphDb() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var user: String = ""
    var pwd: String = ""

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (cfg.has("user")) {
            user = cfg["user"]!!.asString()
        } else {
            logger.fatal("${id} 没有配置user")
            return false
        }

        if (cfg.has("pwd")) {
            pwd = cfg["pwd"]!!.asString()
        } else {
            logger.fatal("${id} 没有配置pwd")
            return false
        }

        if (!cfg.has("host")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["host"]!!.asString()
        if (th.startsWith("unix://")) {
            logger.fatal("${id} java不支持使用管道连接neo4j")
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

    private lateinit var _driver: Driver

    override fun open() {
        _driver = GraphDatabase.driver("neo4j://${host}:${port}", AuthTokens.basic(user, pwd))

        if (execute {
                val v = it.run("match (n) return count(*) as v").single().get("v").asInt()
                logger.log("${id}@neo4j 数据库中存在 ${v} 个节点")
            }) {
            logger.info("连接 ${id}@neo4j")
        } else {
            logger.info("连接 ${id}@neo4j 失败")
        }
    }

    override fun close() {
        _driver.close()
    }

    fun execute(
        proc: (transaction: Transaction) -> Unit,
    ): Boolean {
        var r = true
        try {
            val ses = _driver.session()
            ses.writeTransaction { tx ->
                try {
                    proc(tx)
                } catch (err: Throwable) {
                    logger.exception(err)
                    r = false
                }
                ""
            }
            ses.close()
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        }
        return r
    }

    open fun acquire(): Neo4jSession {
        val ses = _driver.session()
        return Neo4jSession(ses)
    }

    override fun acquireSession(): ISession {
        return acquire()
    }
}

open class Neo4jSession(ses: Session) : ISession {

    private val _ses = ses
    private var _closed = false

    override fun close() {
        if (!_closed) {
            _ses.close()
            _closed = true
        }
    }

    protected fun finalize() {
        close()
    }

    override fun commit() {
        // pass
    }

    override fun rollback() {
        // pass
    }

}