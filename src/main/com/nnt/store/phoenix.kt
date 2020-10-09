package com.nnt.store

import com.fasterxml.jackson.databind.node.ObjectNode
import com.nnt.core.Jsonobj
import com.nnt.core.logger
import java.util.*

// phoenix queryserver 默认端口
private const val DEFAULT_PORT = 8765

class Phoenix : Mybatis() {

    var host: String = ""
    var port: Int = 8765

    override fun config(cfg: Jsonobj): Boolean {
        // mybatis需要外部绑定url，保护一下
        if (!cfg.has("url"))
            (cfg as ObjectNode).put("url", "")
        if (!cfg.has("driver"))
            (cfg as ObjectNode).put("driver", "")

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
        val th = cfg["thin"].asText()
        val sp = th.split(":")
        if (sp.size == 1) {
            host = th
        } else {
            host = sp[0]
            port = sp[1].toInt()
        }

        url = "jdbc:phoenix:thin:url=http://${host}:${port};serialization=PROTOBUF;"

        return true
    }

    override fun verify(): Boolean {
        return execute { _, _ ->
            // pass
        }
    }

    override fun propertiesForJdbc(): Properties {
        val props = super.propertiesForJdbc()
        // phoenix 不支持连接情况检测
        props.setProperty("testWhileIdle", "false")
        return props
    }
}