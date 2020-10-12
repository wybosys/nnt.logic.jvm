package com.nnt.task

import com.nnt.core.Jsonobj

abstract class Task {

    // 任务的id
    var id: String = ""

    // 配置服务
    open fun config(cfg: Jsonobj): Boolean {
        if (!cfg.has("id"))
            return false
        id = cfg["id"].asText()
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