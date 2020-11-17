package com.nnt.core

import com.nnt.manager.Config
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions

interface IRouter {

    // router 的标记
    val action: String

    // 接受配置文件的设置
    fun config(node: JsonObject): Boolean
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

    // 对应的函数对象
    lateinit var func: KFunction<*>
}

annotation class action(val modelType: KClass<*>, val options: Array<String> = [], val comment: String = "")

typealias ActionProtoStore = MutableMap<KClass<*>, MutableMap<String, ActionProto>>

private val _actions: ActionProtoStore = mutableMapOf()

// 添加action定义
fun UpdateAction(clz: KClass<*>, aps: MutableMap<String, ActionProto>) {
    // 读取
    clz.declaredMemberFunctions.forEach { fn ->
        fn.annotations.forEach ann@{ ann ->
            if (ann !is action)
                return@ann

            val ap = ActionProto()
            ap.clazz = ann.modelType
            ap.comment = ann.comment
            ap.func = fn

            ann.options.forEach {
                when (it) {
                    debug -> ap.debug = true
                    develop -> ap.develop = true
                    local -> ap.local = true
                    devops -> ap.devops = true
                    devopsdevelop -> ap.devopsdevelop = true
                    devopsrelease -> ap.devopsrelease = true
                    expose -> ap.expose = true
                }
            }

            var pass = true

            if (ap.develop || ap.develop || ap.local || ap.devops || ap.devopsdevelop || ap.devopsrelease) {
                pass = false
                // 挨个判断是否可以定义
                if (!pass && ap.debug) {
                    if (Config.DEBUG)
                        pass = true
                }
                if (!pass && ap.develop) {
                    if (Config.DEVELOP)
                        pass = true
                }
                if (!pass && ap.local) {
                    if (Config.LOCAL)
                        pass = true
                }
                if (!pass && ap.devops) {
                    if (Config.DEVOPS)
                        pass = true
                }
                if (!pass && ap.devopsdevelop) {
                    if (Config.DEVOPS_DEVELOP)
                        pass = true
                }
                if (!pass && ap.devopsrelease) {
                    if (Config.DEVOPS_RELEASE)
                        pass = true
                }
            }

            // 测试通过，该action生效
            if (pass)
                aps[fn.name] = ap
        }
    }
}

// 查找action定义
fun FindAction(target: Any, key: String): ActionProto? {
    val clz = target.javaClass.kotlin
    var aps = _actions[clz]
    if (aps != null) {
        val ap = aps[key]
        if (ap != null)
            return ap
    }

    if (aps == null) {
        aps = mutableMapOf()
        _actions[clz] = aps
    }

    UpdateAction(clz, aps)

    return aps[key]
}

fun GetAllActionNames(obj: Any): Set<String> {
    val clz = obj.javaClass.kotlin
    var aps = _actions[clz]
    if (aps != null) {
        return aps.keys
    }

    aps = mutableMapOf()
    _actions[clz] = aps
    UpdateAction(clz, aps)

    return aps.keys
}

fun GetAllActions(obj: Any): Map<String, ActionProto> {
    val clz = obj.javaClass.kotlin
    var aps = _actions[clz]
    if (aps != null) {
        return aps
    }

    aps = mutableMapOf()
    _actions[clz] = aps
    UpdateAction(clz, aps)

    return aps
}