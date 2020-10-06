package com.nnt.store

import com.nnt.core.Jsonobj
import com.nnt.core.logger

abstract class AbstractDbms {

    var id: String = ""

    // 配置
    open fun config(cfg: Jsonobj): Boolean {
        if (!cfg.has("id")) {
            logger.fatal("dbms没有配置id")
            return false
        }
        this.id = cfg["id"].asText()
        return true;
    }

    // 打开链接
    abstract suspend fun open()

    // 关闭链接
    abstract suspend fun close()

    // 执行事务
    suspend fun begin() {}
    suspend fun complete() {}
    suspend fun cancel() {}
}
