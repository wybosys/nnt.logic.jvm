package com.nnt.store

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
import java.util.*

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
            logger.fatal("${id} java不支持使用管道连接mysql")
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

    private lateinit var _factory: SqlSessionFactory

    override suspend fun open() {
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

        _factory = SqlSessionFactoryBuilder().build(conf)
        logger.info("连接 ${id}@mysql")
    }

    override suspend fun close() {
        // pass
    }

    suspend fun execute(
        proc: suspend (session: SqlSession) -> Unit
    ) {
        val ses = _factory.openSession(false)
        try {
            proc(ses)
            ses.commit()
        } catch (err: Throwable) {
            logger.exception(err.localizedMessage)
        } finally {
            ses.close()
        }
    }

}
