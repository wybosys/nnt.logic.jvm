package com.nnt.server

import com.nnt.core.Jsonobj
import com.nnt.core.logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration
import java.util.*

class Kafka : Mq() {

    var zk: String = ""
    var group: String = "nnt-logic"

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("zk")) {
            logger.fatal("${id} 没有配置通信地址")
            return false
        }
        zk = cfg["zk"].asText()

        if (cfg.has("group")) {
            group = cfg["group"].asText()
        }
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

    override fun instanceClient(): MqClient {
        return KafkaMqClient(this)
    }

}

class KafkaMqClient(srv: Kafka) : MqClient {

    private val _srv: Kafka = srv
    private var _producer: KafkaProducer<String, String>? = null
    private var _consumer: KafkaConsumer<String, String>? = null
    private var _group: String = srv.group

    lateinit var topic: String

    override fun open(chann: String, opt: MqClientOption?) {
        topic = chann
        if (opt != null) {
            if (opt.group.isNotEmpty())
                _group = opt.group
        }
    }

    override fun close() {
        if (_producer != null) {
            _producer!!.close()
            _producer = null
        }
        if (_consumer != null) {
            _consumer!!.close()
            _consumer = null
        }
    }

    override fun subscribe(cb: (msg: String, chann: String) -> Unit) {
        synchronized(this) {
            if (_consumer == null) {
                val props = Properties()
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, _srv.zk)
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
                props.put(ConsumerConfig.GROUP_ID_CONFIG, _group)

                _consumer = KafkaConsumer<String, String>(props)
            }

            _consumer!!.subscribe(listOf(topic))

            // 开启协程消费数据
            GlobalScope.launch {
                while (true) {
                    val rcds = _consumer!!.poll(Duration.ofSeconds(1))
                    if (!rcds.isEmpty) {
                        rcds.forEach {
                            try {
                                cb(it.value(), topic)
                            } catch (err: Throwable) {
                                logger.exception(err.localizedMessage)
                            }
                        }
                        _consumer!!.commitSync()
                    }
                }
            }
        }
    }

    override fun produce(msg: String) {
        synchronized(this) {
            if (_producer == null) {
                val props = Properties()
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, _srv.zk)
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)

                _producer = KafkaProducer<String, String>(props)
            }

            val rcd = ProducerRecord<String, String>(topic, msg)
            _producer!!.send(rcd)
        }
    }

}
