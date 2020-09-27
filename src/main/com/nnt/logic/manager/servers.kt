package com.nnt.logic.manager

import com.nnt.logic.config.NodeIsEnable
import com.nnt.logic.core.Jsonobj
import com.nnt.logic.core.logger
import com.nnt.logic.server.Server
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class Servers {

    companion object {

        private val _servers = mutableMapOf<String, Server>()

        suspend fun Start(cfg: Jsonobj) {
            if (!cfg.isArray) {
                logger.fatal("server的配置不是数组")
                return
            }

            cfg.forEach() {
                if (!NodeIsEnable(it))
                    return

                val cfg_entry = it["entry"].asText()
                val t = App.shared.instanceEntry(cfg_entry) as Server?
                if (t == null) {
                    println("${cfg_entry} 实例化失败")
                }

                if (t!!.config(it)) {
                    _servers[t.id] = t
                    GlobalScope.async {
                        t.start()
                    }.await()
                } else {
                    println("${t.id} 配置失败")
                }
            }
        }

        suspend fun Stop() {
            for (e in _servers) {
                GlobalScope.async {
                    e.value.stop()
                }.await()
            }
            _servers.clear()
        }

    }
}
