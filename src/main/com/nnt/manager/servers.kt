package com.nnt.manager

import com.nnt.config.NodeIsEnable
import com.nnt.core.Jsonobj
import com.nnt.core.logger
import com.nnt.server.AbstractServer

class Servers {

    companion object {

        private val _servers = mutableMapOf<String, AbstractServer>()

        fun Start(cfg: Jsonobj) {
            if (!cfg.isArray) {
                logger.fatal("server的配置不是数组")
                return
            }

            cfg.forEach() {
                if (!NodeIsEnable(it))
                    return

                val cfg_entry = it["entry"].asText()
                val t = App.shared.instanceEntry(cfg_entry) as AbstractServer?
                if (t == null) {
                    println("${cfg_entry} 实例化失败")
                }

                if (t!!.config(it)) {
                    _servers[t.id] = t
                    t.start()
                } else {
                    println("${t.id} 配置失败")
                }
            }
        }

        fun Stop() {
            for (e in _servers) {
                e.value.stop()
            }
            _servers.clear()
        }

        fun Find(id: String): AbstractServer? {
            return _servers[id]
        }

    }
}
