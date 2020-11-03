package com.nnt.core

import kotlin.reflect.KClass

interface IRouter {

    // router 的标记
    val action: String

    // 接受配置文件的设置
    fun config(node: Jsonobj)
}

// action可用的模式
const val debug = "debug"
const val develop = "develop"
const val local = "local"
const val expose = "expose"
const val devops = "devops"
const val devopsdevelop = "devopsdevelop"
const val devopsrelease = "devopsrelease"

class ActionProto {

    // 绑定的模型类型
    lateinit var clazz: KClass<*>

    // 限制debug可用
    var debug: Boolean = false

    // 限制develop可用
    var develop: Boolean = false

    // 限制local可用
    var local: Boolean = false

    // 限制devops可用
    var devops: Boolean = false

    // 限制devopsdevelop可用
    var devopsdevelop: Boolean = false

    // 限制devopsrelease可用
    var devopsrelease: Boolean = false

    // 注释
    var comment: String = ""

    // 暴露接口
    var expose: Boolean = false
}

annotation class action(val modelType: KClass<*>)
