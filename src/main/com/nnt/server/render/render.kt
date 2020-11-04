package com.nnt.server.render

import com.nnt.core.logger
import com.nnt.server.Transaction
import com.nnt.server.TransactionSubmitOption

abstract class AbstractRender {

    // 输出的类型
    var type: String = ""

    // 渲染数据
    abstract fun render(trans: Transaction, opt: TransactionSubmitOption? = null): ByteArray

}

private val renders = mutableMapOf<String, AbstractRender>()

// 注册渲染组件
fun RegisterRender(name: String, render: AbstractRender) {
    if (renders.containsKey(name)) {
        logger.fatal("重复注册渲染器 $name")
        return
    }
    renders[name] = render
}

fun FindRender(name: String): AbstractRender {
    return renders[name] ?: renders["json"]!!
}
