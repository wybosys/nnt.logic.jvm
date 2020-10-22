package com.nnt.server

import com.nnt.core.Jsonobj
import com.nnt.core.logger
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.KafkaAdminClient
import java.util.*

class Kafka : Mq() {

    var zk: String = ""

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("zk")) {
            logger.fatal("${id} 没有配置通信地址")
            return false
        }

        zk = cfg["zk"].asText()
        return true
    }

    private lateinit var _admin: AdminClient

    override fun start() {
        val props = Properties()
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, zk)
        try {
            _admin = KafkaAdminClient.create(props)
            logger.info("连接 ${id}@kafka")

            /*
            _admin.listTopics().names().thenApply {
                logger.info("共有 ${it.size} 个topics")
            }
             */
        } catch (err: Throwable) {
            logger.exception(err.localizedMessage)
            logger.info("连接 ${id}@kafka 失败")
        }
    }

    override fun stop() {
        _admin.close()
    }

}
