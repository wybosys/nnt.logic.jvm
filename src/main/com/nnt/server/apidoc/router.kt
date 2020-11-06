package com.nnt.server.apidoc

import com.nnt.core.*
import com.nnt.server.Transaction
import kotlin.reflect.KClass

class ParameterInfo {
    lateinit var name: String

    var string: Boolean = false
    var integer: Boolean = false
    var double: Boolean = false
    var number: Boolean = false
    var boolean: Boolean = false
    var file: Boolean = false
    var intfloat: Number? = null
    var isenum: Boolean = false
    var array: Boolean = false
    var map: Boolean = false
    var isobject: Boolean = false
    var optional: Boolean = false

    var index: Int = 0
    var input: Boolean = false
    var output: Boolean = false

    var comment: String = ""

    var valtyp: KClass<*>? = null
    var keytyp: KClass<*>? = null
}

class ActionInfo {
    lateinit var name: String
    lateinit var action: String
    var comment: String = ""
    var params = mutableListOf<ParameterInfo>()
}

@model()
class ExportApis {

    @boolean(1, [input, optional], "生成 logic.node 使用的api")
    var node: Boolean = false

    @boolean(2, [input, optional], "生成 logic.php 使用的api")
    var php: Boolean = false

    @boolean(3, [input, optional], "生成 game.h5 游戏使用api")
    var h5g: Boolean = false

    @boolean(4, [input, optional], "生成 vue 项目中使用的api")
    var vue: Boolean = false
}

class Router : IRouter {

    override val action: String = "api"

    override fun config(node: JsonObject): Boolean {
        return true
    }

    @action(Null::class, [expose], "文档")
    suspend fun doc(trans: Transaction) {
        trans.submit()
    }

    @action(ExportApis::class, [expose], "生成api接口文件")
    suspend fun export(trans: Transaction) {
        trans.submit()
    }
}