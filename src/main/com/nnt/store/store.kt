package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger

abstract class AbstractDbms {

    var id: String = ""

    // 配置
    open fun config(cfg: JsonObject): Boolean {
        if (!cfg.has("id")) {
            logger.fatal("dbms没有配置id")
            return false
        }
        this.id = cfg["id"]!!.asString()
        return true;
    }

    // 打开链接
    abstract fun open()

    // 关闭链接
    abstract fun close()

    // 分配一个session对象
    abstract fun acquireSession(): ISession
}
