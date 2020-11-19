package com.nnt.server.apidoc

import com.nnt.core.*
import com.nnt.thirds.dust.DustCompiler
import kotlin.reflect.KClass

abstract class Export {

    // 输出api使用的dust模板
    var template = "bundle://nnt/server/apidoc/apis.dust"

    // 收集的用来填充模板的参数急
    val params = ExportedParams()

    // 填充模型
    open fun processModels(models: List<KClass<*>>) {
        models.forEach {
            if (FindModel(it) == null) {
                logger.log("跳过生成 ${it.simpleName}: 不是模型")
                return@forEach
            }
            processModel(it)
        }
    }

    open fun processModel(model: KClass<*>) {
        val name = model.simpleName!!
        val mp = FindModel(model)!!
        if (mp.hidden)
            return
        if (mp.enum) {
            val em = ExportedEnum()
            em.name = name
            params.enums.add(em)
            // 枚举得每一项定义都是静态的，所以可以直接遍历
            EnumToMap(model).forEach {
                val t = ExportedPair()
                t.name = it.key
                t.value = it.value
                em.defs.add(t)
            }
        } else if (mp.constant) {
            flat(model).forEach {
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
            val fps = GetAllFields(model)!!
            fps.forEach fps@{ fname, fp ->
                if (fp.inherited)
                    return@fps
                if (fp.id <= 0) {
                    logger.warn("Model的 Field 不能 <=0 ${fname}")
                    return@fps
                }
                if (!fp.input && !fp.output)
                    return@fps
                val t = ExportedField()
                t.name = fname
                t.type = type(fp)
                t.optional = fp.optional
                t.file = fp.file
                t.enum = fp.enum
                t.input = fp.input
                t.deco = field(fp)
                clazz.fields.add(t)
            }
        }
    }

    // 填充路由
    open fun processRouters(routers: List<AbstractRouter>) {
        routers.forEach {
            processRouter(it)
        }
    }

    open fun processRouter(r: AbstractRouter) {
        val aps = GetAllActions(r)
        aps.forEach { name, ap ->
            val t = ExportedRouter()
            t.name = r.action.capitalize() + name.capitalize()
            t.action = r.action.capitalize() + "." + name
            t.type = actionType(ap)
            t.comment = ap.comment
            params.routers.add(t)
        }
    }

    protected var _dust = DustCompiler()

    // 输出
    open suspend fun render(): String {
        if (!_dust.compiled(template)) {
            val src = File(URI(template)).readText()
            if (!_dust.compile(src, template)) {
                logger.error("编译模板 ${template} 失败")
                return ""
            }
        }

        return _dust.render(template, flat(params) as Map<*, *>)
    }

    // 支持类型转换
    abstract fun type(typ: KClass<*>): String

    abstract fun type(fp: FieldOption): String

    // 属性类型转换
    abstract fun fieldType(typ: KClass<*>): String

    // 属性类型定义转换为目标语言类型定义
    abstract fun fieldTypes(fp: FieldOption): String

    // 属性类型选项定义转化
    abstract fun fieldOptions(fp: FieldOption): String

    // 注释转换
    abstract fun fieldComment(fp: FieldOption): String

    // 完整的一个属性转换
    abstract fun field(fp: FieldOption): String

    // 转换动作类型
    abstract fun actionType(ap: ActionProto): String
}

// 导出api需要的数据结构
class ExportedParams {
    val domain = Devops.GetDomain()
    var namespace = ""
    var filename = domain.replace("/", "-") + "-apis"
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