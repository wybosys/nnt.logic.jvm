package com.nnt.store

import com.alibaba.druid.pool.DruidDataSourceFactory
import com.nnt.core.File
import com.nnt.core.JsonObject
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

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (cfg.has("user"))
            user = cfg["user"]!!.asString()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"]!!.asString()

        if (!cfg.has("scheme")) {
            logger.fatal("${id} 没有配置数据表")
            return false
        }
        scheme = cfg["scheme"]!!.asString()

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

        if (cfg.has("mybatis")) {
            val nmb = cfg["mybatis"]!!
            if (!nmb.has("map")) {
                logger.fatal("${id} 没有配置mybatis的map数据")
                return false
            }
            maps = nmb["map"]!!.map { URI(it.asString()) }
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
        return jdbc { tpl ->
            val cnt = tpl.queryForObject("select 1", Int::class.java)
            if (cnt != 1)
                throw Error("mysql连接测试失败")
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
                logger.exception(err)
            }
        }

        _mapfac = SqlSessionFactoryBuilder().build(conf)
    }

    override fun close() {
        // pass
    }

    // 使用mybatis的mapper操作orm数据
    fun mapper(
        proc: (session: SqlSession) -> Unit,
    ): Boolean {
        var r = true
        var ses: SqlSession? = null
        try {
            ses = _mapfac.openSession(false)
            proc(ses)
            ses.commit()
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        } finally {
            ses?.close()
        }
        return r
    }

    // 直接执行sql语句返回原始数据类型
    fun jdbc(
        proc: (tpl: JdbcTemplate) -> Unit,
    ): Boolean {
        var r = true
        try {
            val tpl = JdbcTemplate(_dsfac)
            proc(tpl)
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        }
        return r
    }

    fun acquireJdbc(): JdbcSession {
        val tpl = JdbcTemplate(_dsfac)
        return JdbcSession(tpl)
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }

}
