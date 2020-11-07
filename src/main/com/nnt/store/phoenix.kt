package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import java.sql.Connection
import java.util.*
import kotlin.reflect.KClass

// phoenix queryserver 默认端口
private const val DEFAULT_PORT = 8765

class Phoenix : Mybatis() {

    var host: String = ""
    var port: Int = 8765

    override fun config(cfg: JsonObject): Boolean {
        // mybatis需要外部绑定url，保护一下
        if (!cfg.has("url"))
            cfg["url"] = ""
        if (!cfg.has("driver"))
            cfg["driver"] = ""

        // mybatis初始化
        if (!super.config(cfg))
            return false

        // 不使用driver设置class
        driver = ""

        // 读取地址
        if (!cfg.has("thin")) {
            logger.fatal("${id} 没有配置queryserver地址")
            return false
        }
        val th = cfg["thin"]!!.asString()
        val sp = th.split(":")
        if (sp.size == 1) {
            host = th
        } else {
            host = sp[0]
            port = sp[1].toInt()
        }

        url = "jdbc:phoenix:thin:url=http://${host}:${port};serialization=PROTOBUF;timeZone=Asia/Shanghai;"

        return true
    }

    override fun verify(): Boolean {
        return execute { tpl, _ ->
            val cnt = tpl.queryForObject(
                "select count(*) as cnt from xaas_test", Int::class.java
            )
            logger.log("hbase测试数据库共有 ${cnt} 条记录")
        }
    }

    override fun propertiesForJdbc(): Properties {
        val props = super.propertiesForJdbc()
        // phoenix 不支持连接情况检测
        props.setProperty("testWhileIdle", "false")
        return props
    }

    override fun acquireJdbc(): JdbcSession {
        val conn = _dsfac.connection
        val tpl = JdbcTemplate(SingleConnectionDataSource(conn, true))
        return PhoenixJdbcSession(conn, tpl)
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }
}

// phoenix 5.x 中时间对象需要额外处理，传入time，传出Long，否则会有timezone
// https://developer.aliyun.com/article/684390

class PhoenixJdbcSession(conn: Connection, tpl: JdbcTemplate) : JdbcSession(conn, tpl) {

    override fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>, vararg args: Any): T? {
        if (requiredType == Date::class.java) {
            val r = super.queryForObject(sql, Long::class, *args)
            if (r == null)
                return null

            @Suppress("UNCHECKED_CAST")
            return Date(r.toLong()) as T
        }
        return super.queryForObject(sql, requiredType, *args)
    }

    override fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>): T? {
        if (requiredType == Date::class) {
            val r = super.queryForObject(sql, Long::class)
            if (r == null)
                return null

            @Suppress("UNCHECKED_CAST")
            return Date(r.toLong()) as T
        }
        return super.queryForObject(sql, requiredType)
    }

    override fun <T : Any> queryForObject(
        sql: String,
        args: Array<Any>,
        argTypes: IntArray,
        requiredType: KClass<T>,
    ): T {
        if (requiredType == Date::class.java) {
            val r = super.queryForObject(sql, args, argTypes, Long::class)

            @Suppress("UNCHECKED_CAST")
            return Date(r.toLong()) as T
        }
        return super.queryForObject(sql, args, argTypes, requiredType)
    }

    override fun <T : Any> queryForObject(sql: String, args: Array<Any>, requiredType: KClass<T>): T? {
        if (requiredType == Date::class.java) {
            val r = super.queryForObject(sql, args, Long::class)
            if (r == null)
                return null

            @Suppress("UNCHECKED_CAST")
            return Date(r.toLong()) as T
        }
        return super.queryForObject(sql, args, requiredType)
    }
}