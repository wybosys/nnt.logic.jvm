package com.nnt.task

import com.nnt.core.JsonObject

abstract class Task {

    // 任务的id
    var id: String = ""

    // 配置服务
    open fun config(cfg: JsonObject): Boolean {
        if (!cfg.has("id"))
            return false
        id = cfg["id"]!!.asString()
        return true
    }

    // 执行任务
    open fun start() {
        // pass
    }

    // 停止任务
    open fun stop() {
        // pass
    }
}