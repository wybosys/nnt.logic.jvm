package com.nnt.manager

import com.nnt.config.NodeIsEnable
import com.nnt.core.JsonObject
import com.nnt.core.logger
import com.nnt.task.Task

class Tasks {

    companion object {

        private val _tasks = mutableMapOf<String, Task>()

        fun Start(cfg: JsonObject) {
            if (!cfg.isArray) {
                logger.fatal("server的配置不是数组")
                return
            }

            cfg.forEach { it ->
                if (!NodeIsEnable(it))
                    return@forEach

                val cfg_entry = it["entry"]!!.asString()
                val t = App.shared.instanceEntry(cfg_entry) as Task?
                if (t == null) {
                    println("${cfg_entry} 实例化失败")
                }

                if (t!!.config(it)) {
                    _tasks[t.id] = t
                    t.start()
                } else {
                    println("${t.id} 配置失败")
                }
            }
        }

        fun Stop() {
            for (e in _tasks) {
                e.value.stop()
            }
            _tasks.clear()
        }
    }
}