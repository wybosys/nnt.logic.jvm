package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.ConnectionFactory

private const val DEFAULT_PORT = 2181

class HBase : AbstractKv() {

    // hbase通过zookeeper连接
    var zkhost: String = ""
    var zkport: Int = DEFAULT_PORT
    var zkdir: String = "/hbase"

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("zk")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["zk"]!!.asString()
        val sp = th.split(":")
        if (sp.size == 1) {
            zkhost = th
        } else {
            zkhost = sp[0]
            zkport = sp[1].toInt()
        }

        return true
    }

    private val _conf = HBaseConfiguration.create()

    override fun open() {
        _conf.set("hbase.zookeeper.quorum", zkhost)
        _conf.setInt("hbase.zookeeper.property.clientPort", zkport)
        _conf.set("zookeeper.znode.parent", zkdir)

        if (execute {
                // 通过获取数据判断服务可用性（zk超时或其他原因不一定会抛出异常）
                it.admin.masterInfoPort
            }) {
            logger.info("连接 ${id}@hbase")
        } else {
            logger.info("连接 ${id}@hbase 失败")
        }
    }

    override fun close() {
        // pass
    }

    fun acquire(): HBaseSession {
        val conn = ConnectionFactory.createConnection(_conf)
        return HBaseSession(conn)
    }

    override fun acquireSession(): ISession {
        return acquire()
    }

    fun execute(
        proc: (conn: Connection) -> Unit,
    ): Boolean {
        var r = true
        var conn: Connection? = null
        try {
            conn = ConnectionFactory.createConnection(_conf)
            proc(conn)
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        } finally {
            conn?.close()
        }
        return r
    }

}

open class HBaseSession(conn: Connection) : ISession {

    private val _conn = conn
    private var _closed = false

    override fun close() {
        if (!_closed) {
            _conn.close()
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