package com.nnt.store

import com.alibaba.druid.pool.DruidDataSourceFactory
import com.nnt.core.File
import com.nnt.core.Jsonobj
import com.nnt.core.URI
import com.nnt.core.logger
import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import java.sql.Connection
import java.util.*
import javax.sql.DataSource

private const val DEFAULT_PORT = 3306

class RMysql : AbstractRdb() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var user: String = ""
    var pwd: String = ""
    var scheme: String = ""
    var maps = listOf<URI>()

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false

        if (cfg.has("user"))
            user = cfg["user"].asText()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"].asText()

        if (!cfg.has("scheme")) {
            logger.fatal("${id} 没有配置数据表")
            return false
        }
        scheme = cfg["scheme"].asText()

        if (!cfg.has("host")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["host"].asText()
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

        if (cfg.has("mybatis")) {
            val nmb = cfg["mybatis"]
            if (!nmb.has("map")) {
                logger.fatal("${id} 没有配置mybatis的map数据")
                return false
            }
            maps = nmb["map"].map { URI(it.asText()) }
        }

        return true
    }

    private lateinit var _mapfac: SqlSessionFactory
    private lateinit var _dsfac: DataSource

    override fun open() {
        if (open_jdbc()) {
            open_mapper()
            logger.info("连接 ${id}@mysql")
        } else {
            logger.info("连接 ${id}@mysql 失败")
        }
    }

    // jdbc 直连
    private fun open_jdbc(): Boolean {
        val props = Properties()
        props.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver")
        props.setProperty("url", "jdbc:mysql://${host}:${port}/${scheme}?characterEncoding=UTF-8")
        if (!user.isEmpty()) {
            props.setProperty("username", user)
            if (!pwd.isEmpty())
                props.setProperty("password", pwd)
        }
        _dsfac = DruidDataSourceFactory.createDataSource(props)
        return execute() { tpl, _ ->
            val res = tpl.query("show tables") { rs, _ -> rs.getString(1) }
            logger.log("${id}@mysql 数据库中存在 ${res.size} 张表")
        }
    }

    // 支持mybatis的mapper模式
    private fun open_mapper() {
        // 初始化数据源
        val fac = PooledDataSourceFactory()
        val props = Properties()
        props.setProperty("driver", "com.mysql.cj.jdbc.Driver")
        props.setProperty("url", "jdbc:mysql://${host}:${port}/${scheme}?characterEncoding=UTF-8")
        if (!user.isEmpty()) {
            props.setProperty("username", user)
            if (!pwd.isEmpty())
                props.setProperty("password", pwd)
        }
        fac.setProperties(props)

        // 初始化环境
        val transfac = JdbcTransactionFactory()
        val env = Environment(id, transfac, fac.dataSource)

        // 初始化连接配置
        val conf = Configuration(env)

        // 遍历加入map
        maps.forEach() {
            try {
                val builder =
                    XMLMapperBuilder(
                        File(it).open(),
                        conf,
                        it.path,
                        conf.sqlFragments
                    )
                builder.parse()
            } catch (err: Throwable) {
                logger.exception(err.localizedMessage)
            }
        }

        _mapfac = SqlSessionFactoryBuilder().build(conf)
    }

    override fun close() {
        // pass
    }

    // 使用mybatis的mapper操作orm数据
    fun execute(
        proc: (session: SqlSession) -> Unit
    ): Boolean {
        var r = true
        var ses: SqlSession? = null
        try {
            ses = _mapfac.openSession(false)
            proc(ses)
            ses.commit()
        } catch (err: Throwable) {
            logger.exception(err.localizedMessage)
            r = false
        } finally {
            ses?.close()
        }
        return r
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
