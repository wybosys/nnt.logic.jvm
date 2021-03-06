package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger
import com.nnt.store.reflect.SchemaInfo
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*
import kotlin.random.Random
import kotlin.reflect.KClass

// phoenix queryserver 默认端口
private const val DEFAULT_PORT = 8765

class Phoenix : Mybatis() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var schema: String = ""

    override fun config(cfg: JsonObject): Boolean {
        // 初始化
        slowquery = DEFAULT_PHOENIX_SLOWQUERY

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

        // 设置默认数据库
        if (cfg.has("schema"))
            schema = cfg["schema"]!!.asString().toUpperCase()

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
                throw Error("phoenix 查询链接可用性失败")
            }

            // 创建用于维持连接的数据库
            ses.execute("create table if not exists nnt.logic_phoenix_alive(id integer primary key, val integer)")
            ses.execute("upsert into nnt.logic_phoenix_alive (id, val) values (1, 0)")
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
        val ses = PhoenixJdbcSession(this)
        ses.dataSource = _dsfac
        ses.slowquery = slowquery
        return ses
    }

    override fun propertiesForJdbc(): JdbcProperties {
        val props = super.propertiesForJdbc()
        props.poolName = "nnt.logic.phoneix"
        // phoenix需要自己设置
        props.connectionTestQuery = "select 1"
        // phoneix默认idle时间是60s
        props.idleTimeout = 60000L
        // 设置自动保持连接
        props.keepAliveInterval = 55000L
        props.keepAliveQuery = null // 使用action判断
        props.keepAliveAction = { ses, tpl ->
            PhoenixKeepAliveAction(ses as PhoenixJdbcSession, tpl)
        }
        return props
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }

    override fun tables(): Map<String, com.nnt.store.reflect.TableInfo> {
        return tables(schema)
    }

    override fun table(name: String): com.nnt.store.reflect.TableInfo? {
        return table(schema, name)
    }

    fun schemas(): Map<String, SchemaInfo> {
        val r = mutableMapOf<String, SchemaInfo>()
        jdbc {
            val res = it.queryForList("select distinct table_schem from system.catalog where table_type='u'")
            res.forEach {
                var sch = it["table_schem"]
                if (sch == null)
                    sch = ""
                val si = SchemaInfo()
                si.name = sch as String
                r[si.name] = si
            }
        }
        return r
    }

    fun scheme(scheme: String): SchemaInfo? {
        var r: SchemaInfo? = null
        jdbc {
            val res = it.queryForMap(
                "select distinct table_schem from system.catalog where table_type='u' and table_schem=?",
                scheme.toUpperCase()
            )
            if (res != null) {
                r = SchemaInfo()
                r!!.name = res["table_schem"] as String
            }
        }
        return r
    }

    fun tables(scheme: String): Map<String, com.nnt.store.reflect.TableInfo> {
        val r = mutableMapOf<String, com.nnt.store.reflect.TableInfo>()
        jdbc {
            val res = it.queryForList(
                "select table_name from system.catalog where table_type='u' and table_schem=?",
                scheme.toUpperCase()
            )
            res.forEach {
                val ti = com.nnt.store.reflect.TableInfo()
                ti.name = it["table_name"] as String
                r[ti.name] = ti
            }
        }
        return r
    }

    fun table(scheme: String, name: String): com.nnt.store.reflect.TableInfo? {
        var r: com.nnt.store.reflect.TableInfo? = null
        jdbc {
            val res = it.queryForMap(
                "select table_name from system.catalog where table_type='u' and table_schem=? and table_name=?",
                scheme.toUpperCase(), name.toUpperCase()
            )
            if (res != null) {
                r = com.nnt.store.reflect.TableInfo()
                r!!.name = res["table_name"] as String
            }
        }
        return r
    }
}

// phoenix一般面向大数据系统，慢查询默认放大到1s
val DEFAULT_PHOENIX_SLOWQUERY = 1000L

private fun PhoenixKeepAliveAction(ses: PhoenixJdbcSession, tpl: JdbcTemplate) {
    val old = tpl.queryForObject(
        "select val from nnt.logic_phoenix_alive where id=1",
        Long::class.java
    )

    tpl.update(
        "upsert into nnt.logic_phoenix_alive (id, val) values (1, ?)",
        Random.nextLong(999999999)
    )

    val now = tpl.queryForObject(
        "select val from nnt.logic_phoenix_alive where id=1",
        Long::class.java
    )

    if (now == old) {
        logger.error("${ses.logidr} keepalive失败")
    } else {
        // logger.log("${ses.logidr} keepalive成功")
    }
}

// phoenix 5.x 中时间对象需要额外处理，传入time，传出Long，否则会有timezone
// https://developer.aliyun.com/article/684390

class PhoenixJdbcSession(phoenix: Phoenix) : JdbcSession() {

    private var _phoenix = phoenix

    init {
        logidr = "phoenix"

        // phoenix一般面向大数据系统，慢查询默认放大到1s
        slowquery = DEFAULT_PHOENIX_SLOWQUERY
    }

    val schema: String get() = _phoenix.schema

    override fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>, vararg args: Any): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        if (requiredType == Date::class.java) {
            val r = super.queryForObject(sql, Long::class, *args)
            if (r == null)
                return@metric null

            @Suppress("UNCHECKED_CAST")
            return@metric Date(r.toLong()) as T
        }

        return@metric super.queryForObject(sql, requiredType, *args)
    }

    override fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        if (requiredType == Date::class) {
            val r = super.queryForObject(sql, Long::class)
            if (r == null)
                return@metric null

            @Suppress("UNCHECKED_CAST")
            return@metric Date(r.toLong()) as T
        }

        return@metric super.queryForObject(sql, requiredType)
    }

    override fun <T : Any> queryForObject(
        sql: String,
        args: Array<Any>,
        argTypes: IntArray,
        requiredType: KClass<T>,
    ): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        if (requiredType == Date::class.java) {
            val r = super.queryForObject(sql, args, argTypes, Long::class)
            if (r == null)
                return@metric null

            @Suppress("UNCHECKED_CAST")
            return@metric Date(r.toLong()) as T
        }

        return@metric super.queryForObject(sql, args, argTypes, requiredType)
    }

    override fun <T : Any> queryForObject(sql: String, args: Array<Any>, requiredType: KClass<T>): T? = metric({
        logger.warn("${logidr}-slowquery: cost ${it}ms: ${sql}")
    }) {
        if (requiredType == Date::class.java) {
            val r = super.queryForObject(sql, args, Long::class)
            if (r == null)
                return@metric null

            @Suppress("UNCHECKED_CAST")
            return@metric Date(r.toLong()) as T
        }

        return@metric super.queryForObject(sql, args, requiredType)
    }
}