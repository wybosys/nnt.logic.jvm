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

open class Mybatis : AbstractRdb() {

    var url: String = ""
    var user: String = ""
    var pwd: String = ""
    var driver: String = ""
    var maps = listOf<URI>()

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false
        if (cfg.has("user"))
            user = cfg["user"].asText()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"].asText()

        if (!cfg.has("url")) {
            logger.fatal("${id} 没有配置url")
            return false
        }
        url = cfg["url"].asText()

        if (!cfg.has("driver")) {
            logger.fatal("${id} 没有配置driver")
            return false
        }
        driver = cfg["driver"].asText()

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
        open_jdbc()
        if (verify()) {
            open_mapper()
            logger.info("连接 ${id}@mybatis")
        } else {
            logger.info("连接 ${id}@mybatis 失败")
        }
    }

    // 验证是否打开
    protected open fun verify(): Boolean {
        return true
    }

    private fun open_jdbc() {
        val props = Properties()
        props.setProperty("driverClassName", driver)
        props.setProperty("url", url)
        if (!user.isEmpty()) {
            props.setProperty("username", user)
            if (!pwd.isEmpty())
                props.setProperty("password", pwd)
        }
        _dsfac = DruidDataSourceFactory.createDataSource(props)
    }

    private fun open_mapper() {
        // 初始化数据源
        val fac = PooledDataSourceFactory()
        val props = Properties()
        props.setProperty("driver", driver)
        props.setProperty("url", url)
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
