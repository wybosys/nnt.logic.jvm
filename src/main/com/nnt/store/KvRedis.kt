package com.nnt.store

import com.nnt.core.Jsonobj
import com.nnt.core.logger
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

private const val DEFAULT_PORT = 6379

class KvRedis : AbstractKv() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var prefix: String? = null

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false
        if (!cfg.has("host")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["host"].asText()
        if (th.startsWith("unix://")) {
            logger.fatal("${id} java不支持使用管道连接redis")
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

        if (cfg.has("prefix"))
            prefix = cfg["prefix"].asText()
        return true
    }

    private lateinit var _pool: JedisPool

    override suspend fun open() {
        val cfg = JedisPoolConfig()
        cfg.maxTotal = Runtime.getRuntime().availableProcessors() * 2
        _pool = JedisPool(cfg, host, port)

        if (execute {
                it.ping()
            }) {
            logger.info("连接 ${id}@redis")
        } else {
            logger.info("连接 ${id}@redis 失败")
        }
    }

    suspend fun execute(proc: suspend (redis: Jedis) -> Unit): Boolean {
        var r = true
        try {
            val t = _pool.resource
            try {
                proc(t)
            } catch (err: Throwable) {
                r = false
                logger.exception(err.localizedMessage)
            }
            t.close()
        } catch (err: Throwable) {
            r = false
            logger.exception(err.localizedMessage)
        }
        return r
    }

    override suspend fun close() {
        _pool.close()
    }

}