package com.nnt.server

class MqClientOption {

    // 支持持久化
    var durable = false

    // 长周期化
    var longliving = false

    // 用来广播的客户端
    var transmitter = false

    // 不修改服务器状态
    var passive = false

    // 分组id
    var group: String = ""
}

interface MqClient {

    // 打开通道
    fun open(chann: String, opt: MqClientOption? = null)

    // 关闭通道
    fun close()

    // 订阅消息
    fun subscribe(cb: (msg: String, chann: String) -> Unit)

    // 产生消息
    fun produce(msg: String)
}

abstract class Mq : AbstractServer() {

    // 实例化一个客户端
    abstract fun instanceClient(): MqClient
}