package com.nnt.store

import com.nnt.core.File
import com.nnt.core.JsonObject
import com.nnt.core.URI
import com.nnt.core.logger
import com.nnt.store.reflect.TableInfo
import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import java.util.*

private const val DEFAULT_PORT = 3306

class RMysql : AbstractRdb() {

    var host: String = ""
    var port: Int = DEFAULT_PORT
    var user: String = ""
    var pwd: String = ""
    var schema: String = ""
    var maps = listOf<URI>()

    override fun tables(): Map<String, TableInfo> {
        val r = mutableMapOf<String, TableInfo>()
        jdbc {
            val res = it.queryForList(
                "show tables"
            )
            res.forEach {
                val nm = it.values.first()
                val ti = TableInfo()
                ti.name = nm as String
                r[ti.name] = ti
            }
        }
        return r
    }

    override fun table(name: String): TableInfo? {
        var ti: TableInfo? = null
        jdbc {
            val res = it.queryForList(
                "describe ${name}"
            )

            if (res.size != 0) {
                ti = TableInfo()
                ti!!.name = name
            }
        }
        return ti
    }

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (cfg.has("user"))
            user = cfg["user"]!!.asString()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"]!!.asString()

        if (!cfg.has("schema")) {
            logger.fatal("${id} 没有配置数据表")
            return false
        }
        schema = cfg["schema"]!!.asString()

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

    // 提取mybatis-mapper的对象
    private lateinit var _mapfac: SqlSessionFactory

    // 数据源+连接池
    private lateinit var _dsfac: JdbcDataSource

    override fun open() {
        if (openJdbc()) {
            openMapper()
            logger.info("连接 ${id}@mysql")
        } else {
            logger.info("连接 ${id}@mysql 失败")
        }
    }

    // jdbc 直连
    private fun openJdbc(): Boolean {
        val props = Jdbc.DefaultJdbcProperties()
        props.driverClassName = "com.mysql.cj.jdbc.Driver"
        props.jdbcUrl = "jdbc:mysql://${host}:${port}/${schema}?characterEncoding=UTF-8"
        if (!user.isEmpty()) {
            props.username = user
            if (!pwd.isEmpty())
                props.password = pwd
        }
        _dsfac = JdbcDataSource(props)
        return jdbc { ses ->
            val cnt = ses.queryForObject(
                "select 1",
                Int::class
            )
            if (cnt != 1)
                throw Error("mysql连接测试失败")
        }
    }

    // 支持mybatis的mapper模式
    private fun openMapper() {
        // 初始化数据源
        val fac = PooledDataSourceFactory()
        val props = Properties()
        props.setProperty("driver", "com.mysql.cj.jdbc.Driver")
        props.setProperty("url", "jdbc:mysql://${host}:${port}/${schema}?characterEncoding=UTF-8")
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
        _dsfac.close()
    }

    // 使用mybatis的mapper操作orm数据
    fun mapper(
        proc: (session: MybatisSession) -> Unit,
    ): Boolean {
        var r = true
        var ses: SqlSession? = null
        try {
            ses = _mapfac.openSession(false)
            proc(MybatisSession(ses))
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
        proc: (ses: JdbcSession) -> Unit,
    ): Boolean {
        var r = true
        try {
            val ses = JdbcSession()
            ses.dataSource = _dsfac
            proc(ses)
            ses.close()
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        }
        return r
    }

    fun acquireJdbc(): JdbcSession {
        val ses = JdbcSession()
        ses.dataSource = _dsfac
        return ses
    }

    override fun acquireSession(): ISession {
        return acquireJdbc()
    }

}
