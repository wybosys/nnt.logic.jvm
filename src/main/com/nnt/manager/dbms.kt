package com.nnt.manager

import com.nnt.config.NodeIsEnable
import com.nnt.core.Jsonobj
import com.nnt.core.logger
import com.nnt.store.AbstractDbms
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

private val dbs = mutableMapOf<String, AbstractDbms>()

class Dbms {

    companion object {

        suspend fun Start(cfg: Jsonobj) {
            if (!cfg.isArray) {
                logger.fatal("dbms的配置不是数组")
                return
            }

            cfg.forEach() {
                if (!NodeIsEnable(it))
                    return

                val cfg_entry = it["entry"].asText()
                val t = App.shared.instanceEntry(cfg_entry) as AbstractDbms?
                if (t == null) {
                    println("${cfg_entry} 实例化失败")
                }

                if (t!!.config(it)) {
                    dbs[t.id] = t
                    GlobalScope.async {
                        t.open()
                    }.await()
                } else {
                    println("${t.id} 配置失败")
                }
            }
        }

        suspend fun Stop() {
            for (e in dbs) {
                GlobalScope.async {
                    e.value.close()
                }.await()
            }
            dbs.clear()
        }
    }
}