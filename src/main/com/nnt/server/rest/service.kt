package com.nnt.server.rest

import com.nnt.core.IRouter
import com.nnt.core.JsonObject
import com.nnt.core.RestUpdate
import com.nnt.core.action
import com.nnt.server.Transaction

const val WEAK_HEARTBEAT = 30

// RestService用来解决如下问题
// 1，当model是异步连接模式时，客户端请求会被立即返回。当服务端逻辑运行结束后，得到的结果将缓存在服务器中，等待客户端使用提供的SDK刷新此接口，或者当客户端请求其他同步接口时返回
// 2，和ClientSDK配合，维持一个弱心跳(每一次心跳的间隔时间通过服务端返回，这样可以通过对api请求的分析计算最优的心跳间隔)，该数据通道用来使用rest来模仿socket

class RestService : IRouter {

    override var action = "rest"

    override fun config(node: JsonObject) {
        // pass
    }

    @action(RestUpdate::class)
    suspend fun update(trans: Transaction) {
        val m = trans.model as RestUpdate

        // 弱心跳的时间
        m.heartbeatTime = WEAK_HEARTBEAT;

        trans.submit()
    }
}
