package com.nnt.store

import com.nnt.core.Jsonobj
import com.nnt.core.logger
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Transaction

private const val DEFAULT_PORT = 7687

class Neo4J : AbstractGraphDb() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var user: String = ""
    var pwd: String = ""

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false

        if (cfg.has("user")) {
            user = cfg["user"].asText()
        } else {
            logger.fatal("${id} 没有配置user")
            return false
        }

        if (cfg.has("pwd")) {
            pwd = cfg["pwd"].asText()
        } else {
            logger.fatal("${id} 没有配置pwd")
            return false
        }

        if (!cfg.has("host")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["host"].asText()
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
        _driver = GraphDatabase.driver("bolt://${host}:${port}", AuthTokens.basic(user, pwd))

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
        proc: (transaction: Transaction) -> Unit
    ): Boolean {
        var r = true
        try {
            val ses = _driver.session()
            ses.writeTransaction { tx ->
                try {
                    proc(tx)
                } catch (err: Throwable) {
                    logger.exception(err.localizedMessage)
                    r = false
                }
                ""
            }
        } catch (err: Throwable) {
            logger.exception(err.localizedMessage)
            r = false
        }
        return r
    }
}
