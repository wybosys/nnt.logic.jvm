package com.nnt.server.apidoc

import com.nnt.core.*
import com.nnt.manager.App
import com.nnt.server.IRouterable
import com.nnt.server.RespFile
import com.nnt.server.Routers
import com.nnt.server.Transaction
import com.nnt.thirds.dust.DustCompiler
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

class Router : AbstractRouter() {

    override val action: String = "api"

    // 导出设置的路由和模型
    private var _routers = listOf<AbstractRouter>()
    private var _models = listOf<KClass<*>>()

    override fun config(node: JsonObject): Boolean {
        _page = File(URI("bundle://nnt/server/apidoc/apidoc.volt")).readText()

        if (node.has("export")) {
            try {
                val nexp = node["export"]!!
                _routers = nexp["router"]!!.asArray().map {
                    App.shared.instanceEntry(it.asString()) as AbstractRouter
                }

                val mdls = mutableListOf<KClass<*>>()
                nexp["model"]!!.asArray().forEach {
                    val e = it.asString()
                    // 如果是带*号，则拉出该package下所有的model
                    if (e.endsWith(".*")) {
                        val pkg = Jvm.LoadPackage(e.substring(0, e.length - 2))!!
                        pkg.filter {
                            FindModel(it.clazz) != null
                        }
                        mdls.addAll(mdls.size, pkg.sorted().map {
                            it.clazz
                        })
                    } else {
                        mdls.add(App.shared.findEntry(e)!!)
                    }
                }
                _models = mdls
            } catch (err: Throwable) {
                logger.exception(err)
                return false
            }
        }

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

    // dust生成文档模板
    private var _dust = DustCompiler()

    @action(ExportApis::class, [expose], "生成api接口文件")
    suspend fun export(trans: Transaction) {
        val m = trans.model as ExportApis
        if (!m.node && !m.php && !m.h5g && !m.vue) {
            trans.status = STATUS.PARAMETER_NOT_MATCH
            trans.submit()
            return
        }

        // 分析出的所有结构
        val params = ExportedParams()
        if (m.php) {
            val sp = params.domain.split('/')
            params.namespace = sp[0].capitalize() + "\\" + sp[1].capitalize()
        }

        // 便利所有模型，生成模型段
        _models.forEach { clz ->
            if (FindModel(clz) == null) {
                logger.log("跳过生成 ${clz.simpleName}: 不是模型")
                return@forEach
            }

            val name = clz.simpleName!!
            val mp = FindModel(clz)!!
            if (mp.hidden)
                return@forEach
            if (mp.enum) {
                val em = ExportedEnum()
                em.name = name
                params.enums.add(em)
                // 枚举得每一项定义都是静态的，所以可以直接遍历
                EnumToMap(clz).forEach {
                    val t = ExportedPair()
                    t.name = it.key
                    t.value = it.value
                    em.defs.add(t)
                }
            } else if (mp.constant) {
                flat(clz).forEach {
                    val t = ExportedConst()
                    t.name = name.toUpperCase() + "_" + it.key.toUpperCase()
                    t.value = it.value
                    params.consts.add(t)
                }
            } else {
                // 判断是否有父类
                val clazz = ExportedClazz()
                clazz.name = name
                clazz.super_ = mp.parent?.simpleName ?: "ApiModel"
                params.clazzes.add(clazz)
                // 填充fields信息
                val fps = GetAllFields(clz)!!
                fps.forEach fps@{ fname, fp ->
                    if (fp.inherited)
                        return@fps
                    if (fp.id <= 0) {
                        logger.warn("Model的 Field 不能 <=0 ${fname}")
                        return@fps
                    }
                    if (!fp.input && !fp.output)
                        return@fps
                    val type = FpToTypeDef(fp)
                    val deco: String
                    if (m.php) {
                        deco = FpToDecoDefPHP(fp)
                    } else {
                        deco = FpToDecoDef(fp, "Model.")
                    }
                    val t = ExportedField()
                    t.name = fname
                    t.type = type
                    t.optional = fp.optional
                    t.file = fp.file
                    t.enum = fp.enum
                    t.input = fp.input
                    t.deco = deco
                    clazz.fields.add(t)
                }
            }
        }

        // 遍历所有接口，生成接口段
        _routers.forEach { router ->
            val aps = GetAllActions(router)
            aps.forEach { name, ap ->
                val t = ExportedRouter()
                t.name = router.action.capitalize() + name.capitalize()
                t.action = router.action.capitalize() + "." + name
                val cn = ap.clazz.simpleName!!
                if (m.vue || m.node) {
                    t.type = cn
                } else if (m.php) {
                    t.type = "M$cn"
                } else {
                    t.type = "models.$cn"
                }
                t.comment = ap.comment
                params.routers.add(t)
            }
        }

        // 渲染模板
        var apis = "bundle://nnt/server/apidoc/"
        if (m.node) {
            apis += "apis-node.dust"
        } else if (m.h5g) {
            apis += "apis-h5g.dust"
        } else if (m.vue) {
            apis += "apis-vue.dust"
        } else if (m.php) {
            apis += "apis-php.dust"
        } else {
            apis += "apis.dust"
        }

        if (!_dust.compiled(apis)) {
            val src = File(URI(apis)).readText()
            if (!_dust.compile(src, apis)) {
                throw Error("编译模板失败")
            }
        }

        var out = _dust.render(apis, flat(params) as Map<*, *>)
        // 需要加上php的头
        if (m.php) {
            out = "<?php\n" + out
        }

        var apifile = params.domain.replace("/", "-") + "-apis"
        if (m.php) {
            apifile += ".php"
        } else {
            apifile += ".ts"
        }

        // 输出到客户端
        trans.output("text/plain", RespFile.Plain(out).asDownload(apifile));
    }
}

// 保存action的信息
typealias ActionInfo = MutableMap<String, Any?>
typealias ParameterInfo = MutableMap<String, Any?>

// 避免重复分析
private val _actioninfos = mutableMapOf<String, List<ActionInfo>>()

// 提取路由所有的动作信息
private fun ActionsInfo(routers: Routers): List<ActionInfo> {
    val r = mutableListOf<ActionInfo>()
    routers.forEach { _, v ->
        RouterActions(v).forEach {
            r.add(it)
        }
    }
    return r
}

private fun RouterActions(router: AbstractRouter): List<ActionInfo> {
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

// 提取动作的属性信息
private fun ParametersInfo(clz: KClass<*>): List<ParameterInfo> {
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

// 导出api需要的数据结构
class ExportedParams {
    val domain = Devops.GetDomain()
    var namespace = ""
    var clazzes = mutableListOf<ExportedClazz>()
    var enums = mutableListOf<ExportedEnum>()
    var consts = mutableListOf<ExportedConst>()
    var routers = mutableListOf<ExportedRouter>()
}

class ExportedClazz {
    var name = ""
    var super_ = ""
    var fields = mutableListOf<ExportedField>()
}

class ExportedField {
    var name = ""
    var type = ""
    var optional = false
    var file = false
    var enum = false
    var input = false
    var deco = ""
}

class ExportedEnum {
    var name = ""
    var defs = mutableListOf<ExportedPair>()
}

class ExportedPair {
    var name = ""
    var value: Any? = null
}

class ExportedConst {
    var name = ""
    var value: Any? = null
}

class ExportedRouter {
    var name = ""
    var action = ""
    var type = ""
    var comment = ""
}