package com.nnt.logic.server

import com.nnt.logic.core.JsonObject
import com.nnt.logic.core.logger
import com.nnt.logic.manager.App
import com.nnt.logic.thirds.dubbo.ServiceConfig
import org.apache.dubbo.config.ApplicationConfig
import org.apache.dubbo.config.ProtocolConfig
import org.apache.dubbo.config.RegistryConfig

private class DubboRegistryCfg {
    var type: String = ""
    var host: String = ""
}

private class DubboProtocol {
    var type: String = ""
    var services = mutableListOf<DubboService>()
    var port: Int = 0
    var threads: Int = 0
}

private class DubboService {
    var id: String = ""
    lateinit var impl: String
    lateinit var iface: String
    lateinit var svccfg: ServiceConfig
}

open class Dubbo : Server() {

    private lateinit var _name: String
    private lateinit var _registry: DubboRegistryCfg
    private val _protocols = mutableMapOf<String, DubboProtocol>()
    private val _services = mutableMapOf<String, DubboService>()

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        // 设置应用配置
        _name = (cfg["name"] ?: cfg["id"]).asText()

        // 获取注册中心配置
        if (!cfg.has("registry")) {
            logger.fatal("没有配置registry")
            return false
        }

        val cfg_reg = cfg["registry"]
        if (cfg_reg["type"] == null) {
            logger.fatal("registry丢失type配置")
            return false
        }

        val cfg_reg_type = cfg_reg["type"].asText()
        when (cfg_reg_type) {
            "zookeeper" -> {
                _registry = DubboRegistryCfg()
                _registry.type = cfg_reg_type
                val host = cfg_reg["host"].asText()
                _registry.host = "zookeeper://${host}"
            }
            else -> {
                logger.fatal("registry不支持该类型 ${cfg_reg_type}")
                return false
            }
        }

        // 获取服务配置
        _services.clear()
        val cfg_service = cfg["service"]
        if (cfg_service == null || !cfg_service.isArray) {
            logger.fatal("没有配置service")
            return false
        }
        for (e in cfg_service) {
            val svc = DubboService()
            svc.id = e["id"].asText()

            var clsnm = e["impl"].asText()
            var cls = App.shared.findEntry(clsnm)
            if (cls == null) {
                logger.fatal("没有找到类型 ${clsnm}")
                return false
            }
            svc.impl = clsnm

            clsnm = e["iface"].asText()
            cls = App.shared.findEntry(clsnm)
            if (cls == null) {
                logger.fatal("没有找到类型 ${clsnm}")
                return false
            }
            svc.iface = clsnm

            _services[svc.id] = svc
        }

        // 获取协议配置
        _protocols.clear()
        val cfg_protocol = cfg["protocol"]
        if (cfg_protocol == null || !cfg_protocol.isArray) {
            logger.fatal("没有配置protocol")
            return false
        }
        for (e in cfg_protocol) {
            val pt = DubboProtocol()

            pt.type = e["type"].asText()
            when (pt.type) {
                "dubbo" -> {
                    pt.port = e["port"].asInt()
                    pt.threads = e["threads"].asInt(1)
                }
                "rest" -> {
                    pt.port = e["port"].asInt()
                }
                else -> {
                    logger.fatal("不支持该协议 ${pt.type}")
                    return false
                }
            }

            val svc = e["service"]
            if (svc == null || !svc.isArray) {
                logger.fatal("service数据错误")
                return false
            }
            for (esvc in svc) {
                val svcid = esvc.asText()
                val fnd = _services[svcid]
                if (fnd == null) {
                    logger.fatal("没有找到服务 ${svcid}")
                    return false
                }

                pt.services.add(fnd)
            }

            _protocols[pt.type] = pt
        }

        return true
    }

    override suspend fun start() {
        val app = ApplicationConfig()
        app.name = _name

        val reg = RegistryConfig()
        reg.protocol = _registry.type
        reg.address = _registry.host

        val svcs = mutableMapOf<String, ServiceConfig>()
        for (e in _services) {
            val impl = App.shared.instanceEntry(e.value.impl)!!

            val svc = ServiceConfig()
            svc.application = app
            svc.registry = reg
            svc.`interface` = e.value.iface
            svc.version = "1.0.0"
            svc.ref = impl
            svc.serviceClass = impl.javaClass
            svc.protocols = mutableListOf()

            svcs[e.key] = svc
            e.value.svccfg = svc
        }

        for (e in _protocols) {
            val p = ProtocolConfig()
            p.name = e.key
            p.port = e.value.port
            when (e.key) {
                "dubbo" -> {
                    p.threads = e.value.threads
                    p.server = "netty"
                }
                "rest" -> {
                    p.server = "netty"
                }
            }
            for (ecfg in e.value.services) {
                val svc = svcs[ecfg.id]!!
                svc.protocols.add(p)
            }
        }

        for (e in svcs) {
            e.value.export()
        }

        logger.info("启动 ${id}@rest")
    }

    override suspend fun stop() {
        for (e in _services) {
            e.value.svccfg.unexport()
        }

        _protocols.clear()
        _services.clear()
    }
}