package com.nnt.store

import com.nnt.core.*
import com.nnt.store.reflect.SchemeInfo
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*
import javax.sql.DataSource
import kotlin.random.Random
import kotlin.reflect.KClass

// phoenix queryserver 默认端口
private const val DEFAULT_PORT = 8765

class Phoenix : Mybatis() {

    var host: String = ""
    var port: Int = 8765
    var scheme: String = ""

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

        // 设置默认数据库
        if (cfg.has("scheme"))
            scheme = cfg["scheme"]!!.asString().toUpperCase()

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
    
    override fun acquireJdbc(): JdbcSession {
        return PhoenixJdbcSession(this)
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }

    override fun tables(): Map<String, com.nnt.store.reflect.TableInfo> {
        return tables(scheme)
    }

    override fun table(name: String): com.nnt.store.reflect.TableInfo? {
        return table(scheme, name)
    }

    fun schemes(): Map<String, SchemeInfo> {
        val r = mutableMapOf<String, SchemeInfo>()
        jdbc {
            val res = it.queryForList("select distinct table_schem from system.catalog where table_type='u'")
            res.forEach {
                var sch = it["table_schem"]
                if (sch == null)
                    sch = ""
                val si = SchemeInfo()
                si.name = sch as String
                r[si.name] = si
            }
        }
        return r
    }

    fun scheme(scheme: String): SchemeInfo? {
        var r: SchemeInfo? = null
        jdbc {
            val res = it.queryForMap(
                "select distinct table_schem from system.catalog where table_type='u' and table_schem=?",
                scheme.toUpperCase()
            )
            if (res != null) {
                r = SchemeInfo()
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

// phoenix 5.x 中时间对象需要额外处理，传入time，传出Long，否则会有timezone
// https://developer.aliyun.com/article/684390

class PhoenixJdbcSession(phoenix: Phoenix) : JdbcSession() {

    private var _repeat: RepeatHandler? = null
    private var _ds: DataSource
    private var _phoenix = phoenix

    init {
        _ds = _phoenix.openJdbc()
        _tpl = JdbcTemplate(_ds)

        // phoenix thin queryserver 每5分钟需要重连一次，避免掉线，暂时没有找到原因
        startKeepAlive()
    }

    val scheme: String get() = _phoenix.scheme

    fun startKeepAlive() {
        if (_repeat != null)
            return

        _repeat = Repeat(60) {
            val tpl = _tpl!!
            synchronized(this) {
                // _ds = _phoenix.openJdbc()
                // _tpl = JdbcTemplate(_ds)

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

                if (old == now) {
                    logger.error("phoenix出现写入失败, 重新建立链接")

                    _ds = _phoenix.openJdbc()
                    _tpl = JdbcTemplate(_ds)
                }

                // logger.log("phoenix keepalive")
            }
        }
    }

    fun stopKeepAlive() {
        if (_repeat != null) {
            CancelRepeat(_repeat!!)
            _repeat = null
        }
    }

    override fun close() {
        super.close()
        stopKeepAlive()
    }

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
    ): T? {
        if (requiredType == Date::class.java) {
            val r = super.queryForObject(sql, args, argTypes, Long::class)
            if (r == null)
                return null

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