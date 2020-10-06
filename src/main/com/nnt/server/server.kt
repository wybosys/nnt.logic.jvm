package com.nnt.server

import com.nnt.core.Jsonobj

abstract class Server {

    // 服务器的配置id
    var id: String = ""

    // 配置服务
    open fun config(cfg: Jsonobj): Boolean {
        if (!cfg.has("id"))
            return false
        id = cfg["id"].asText()
        return true
    }

    // 启动服务
    abstract suspend fun start()

    // 停止服务
    abstract suspend fun stop()

    // 回调
    protected fun onStart() {
        // pass
    }

    protected fun onStop() {
        // pass
    }
}
