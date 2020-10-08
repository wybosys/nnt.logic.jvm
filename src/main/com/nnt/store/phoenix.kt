package com.nnt.store

import com.fasterxml.jackson.databind.node.ObjectNode
import com.nnt.core.Jsonobj
import com.nnt.core.logger

class Phoenix : Mybatis() {

    var zkhost: String = ""

    override fun config(cfg: Jsonobj): Boolean {
        // mybatis需要外部绑定url，保护一下
        if (!cfg.has("url"))
            (cfg as ObjectNode).putNull("url")

        if (!cfg.has("driver"))
            (cfg as ObjectNode).put("driver", "org.apache.phoenix.jdbc.PhoenixDriver")

        // mybatis初始化
        if (!super.config(cfg))
            return false

        if (!cfg.has("zk")) {
            logger.fatal("${id} 没有配置zk地址")
            return false
        }
        zkhost = cfg["zk"].asText()

        url = "jdbc:phoenix:${zkhost}"
        return true
    }
}