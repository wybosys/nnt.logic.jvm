package com.nnt.core

interface IRouter {

    // router 的标记
    val action: String

    // 接受配置文件的设置
    fun config(node: Jsonobj)
}