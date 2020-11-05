package com.nnt.manager

import com.nnt.config.NodeIsEnable
import com.nnt.core.JsonObject
import com.nnt.core.logger
import com.nnt.store.AbstractDbms

private val dbs = mutableMapOf<String, AbstractDbms>()

class Dbms {

    companion object {

        fun Start(cfg: JsonObject) {
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
                    t.open()
                } else {
                    println("${t.id} 配置失败")
                }
            }
        }

        fun Stop() {
            for (e in dbs) {
                e.value.close()
            }
            dbs.clear()
        }

        fun Find(id: String): AbstractDbms? {
            return dbs[id]
        }
    }
}