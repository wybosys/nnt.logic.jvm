package com.nnt.core

import com.nnt.manager.Config
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions

abstract class AbstractRouter {

    // router 的标记
    open val action: String = ""

    // 接受配置文件的设置
    open fun config(node: JsonObject): Boolean {
        // pass
        return true
    }

    // 路由启动，一些持久化的事物处理必须通过start/stop关停，而不能写到init中，避免apidoc之类生成文档需要构造router实例造成多次启动
    open fun start() {
        // pass
    }

    // 路由停止
    open fun stop() {
        // pass
    }
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

typealias ActionsProto = MutableMap<String, ActionProto>
typealias RouterProtoStore = MutableMap<KClass<*>, ActionsProto?>

// 保存查找过的类定义，类不是router的话，保存null
private val _routers: RouterProtoStore = mutableMapOf()

// 更新Action定义
fun UpdateAction(clz: KClass<*>): MutableMap<String, ActionProto>? {
    var r: MutableMap<String, ActionProto>? = null

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
            if (pass) {
                if (r == null)
                    r = mutableMapOf()
                r!![fn.name] = ap
            }
        }
    }

    return r
}

// 查找router定义
fun FindRouterActons(clz: KClass<*>): ActionsProto? {
    if (_routers.contains(clz)) {
        return _routers[clz]
    }

    val ap = UpdateAction(clz)
    _routers[clz] = ap
    return ap
}

// 查找action定义
fun FindAction(target: Any, key: String): ActionProto? {
    val clz = target.javaClass.kotlin
    val ap = FindRouterActons(clz)
    return ap?.get(key)
}

fun GetAllActionNames(target: Any): Set<String> {
    val clz = target.javaClass.kotlin
    val ap = FindRouterActons(clz)
    return ap?.keys ?: setOf()
}

fun GetAllActions(target: Any): ActionsProto {
    return FindRouterActons(target.javaClass.kotlin)!!
}