package com.nnt.server

import com.nnt.acl.AcEntity
import com.nnt.core.JsonObject

abstract class AbstractServer {

    // 服务器的配置id
    var id: String = ""

    // 配置服务
    open fun config(cfg: JsonObject): Boolean {
        if (!cfg.has("id"))
            return false
        id = cfg["id"]!!.asString()
        return true
    }

    // 启动服务
    abstract fun start()

    // 停止服务
    abstract fun stop()

    // 回调
    protected fun onStart() {
        // pass
    }

    protected fun onStop() {
        // pass
    }
}

// 如果需要在业务中的api调用某一个服务(使用Servers.Call函数)则目标server必须实现此接口
interface IConsoleServer {

    // 通过控制台执行
    // @params 调用参数
    // @req 请求对象
    // @rsp 响应对象
    fun invoke(params: Map<String, Any?>, req: Any, rsp: Any, ac: AcEntity? = null)

}