package com.nnt.server.apidoc

import com.nnt.core.*
import com.nnt.server.IRouterable
import com.nnt.server.Routers
import com.nnt.server.Transaction
import kotlin.reflect.KClass

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
        _page = File(URI("bundle://nnt/server/apidoc/apidoc.volt")).readText()
        return true
    }

    // volt模板
    private lateinit var _page: String

    @action(Null::class, [expose], "文档")
    suspend fun doc(trans: Transaction) {
        val srv = trans.server as IRouterable
        if (srv.routers.size > 0) {
            // 收入及routers的信息
            val infos = ActionsInfo(srv.routers)
            // 渲染页面
            val json = toJson(infos)
            val content = _page.replace("{{actions}}", json)
            trans.output("text/html;charset=utf-8;", content)
        } else {
            trans.status = STATUS.NEED_ITEMS
            trans.submit()
        }
    }

    @action(ExportApis::class, [expose], "生成api接口文件")
    suspend fun export(trans: Transaction) {
        trans.submit()
    }
}

typealias ActionInfo = MutableMap<String, Any?>
typealias ParameterInfo = MutableMap<String, Any?>

private val _actioninfos = mutableMapOf<String, List<ActionInfo>>()

fun ActionsInfo(routers: Routers): List<ActionInfo> {
    val r = mutableListOf<ActionInfo>()
    routers.forEach { v, _ ->
        RouterActions(v).forEach {
            r.add(it)
        }
    }
    return r
}

fun RouterActions(router: IRouter): List<ActionInfo> {
    val name = router.action

    synchronized(_actioninfos) {
        if (_actioninfos.contains(name))
            return _actioninfos[name]!!
    }

    // 获得router身上的action信息以及属性列表
    val names = GetAllActionNames(router)
    val infos = names.map {
        val ap = FindAction(router, it)!!
        val t: ActionInfo = mutableMapOf()
        t["name"] = "${name}.${it}"
        t["action"] = t["name"]
        t["comment"] = ap.comment
        t["params"] = ParametersInfo(ap.clazz)
        t
    }

    synchronized(_actioninfos) {
        _actioninfos[name] = infos
    }

    return infos
}

fun ParametersInfo(clz: KClass<*>): List<ParameterInfo> {
    val fps = GetAllFields(clz)!!
    val r = mutableListOf<ParameterInfo>()
    fps.forEach { name, fp ->
        val t: ParameterInfo = mutableMapOf()
        t["name"] = name
        t["array"] = fp.array
        t["string"] = fp.string
        t["integer"] = fp.integer
        t["decimal"] = fp.decimal
        t["intfloat"] = fp.intfloat
        t["boolean"] = fp.boolean
        t["file"] = fp.file
        t["enum"] = fp.enum
        t["array"] = fp.array
        t["map"] = fp.map
        t["object"] = fp.json
        t["optional"] = fp.optional
        t["index"] = fp.id
        t["input"] = fp.input
        t["output"] = fp.output
        t["comment"] = fp.comment
        t["valtyp"] = fp.valtype?.simpleName ?: ""
        t["keytyp"] = fp.keytype?.simpleName ?: ""
        r.add(t)
    }
    return r
}