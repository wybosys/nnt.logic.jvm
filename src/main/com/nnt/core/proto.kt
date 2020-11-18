package com.nnt.core

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties

// model的参数

// 隐藏该model
const val hidden = "hidden"

// 需要登录
const val auth = "auth"

// 是一个枚举
const val enumm = "enumm"

// 定义一组const对象
const val constant = "constant"

// field的参数
// 可选的参数
const val optional = "optional"

// 必须的参数，不提供则忽略
const val required = "required"

// 输入输出
const val input = "input"
const val output = "output"

class ModelOption {

    // 需要登陆验证
    var auth: Boolean = false

    // 是否是枚举类型，因为语言限制，无法对enum对象添加decorate处理，只能在服务器端使用class来模拟
    var enum: Boolean = false

    // 用来定义常量，或者模拟str的枚举
    var constant: Boolean = false

    // 隐藏后就不会加入到models列表中
    var hidden: Boolean = false

    // 父类，目前用来生成api里面的父类名称
    var parent: KClass<*>? = null

}

interface FieldValidProc {

    // 可以附加一个错误码
    var status: STATUS

    // 当返回false时，即为验证失败，此时上层可以通过获取status来返回特定的错误状态码
    fun valid(inp: Any?): Boolean
}

class FieldOption {

    // 唯一序号，后续类似pb的协议会使用id来做数据版本兼容
    var id: Int = 0

    // 继承过来的属性
    var inherited: Boolean = false

    // 可选
    var optional: Boolean = false

    // 读取控制
    var input: Boolean = false
    var output: Boolean = false

    // 类型标签
    var array: Boolean = false
    var map: Boolean = false
    var string: Boolean = false
    var integer: Boolean = false
    var decimal: Boolean = false
    var boolean: Boolean = false
    var enum: Boolean = false
    var file: Boolean = false
    var json: Boolean = false
    var filter: Boolean = false
    var intfloat: Float? = null
    var timestamp: Boolean = false

    // 关联类型
    var keytype: KClass<*>? = null
    var valtype: KClass<*>? = null

    var comment: String = "" // 注释

    // 有效性检查函数
    var valid: FieldValidProc? = null

    // 对应的属性设置，用来绑定数据到对象
    lateinit var property: KProperty<*>

    fun clone(): FieldOption {
        val r = FieldOption()
        r.id = id
        r.inherited = inherited
        r.optional = optional
        r.input = input
        r.output = output
        r.array = array
        r.map = map
        r.string = string
        r.integer = integer
        r.decimal = decimal
        r.boolean = boolean
        r.enum = enum
        r.file = file
        r.json = json
        r.filter = filter
        r.intfloat = intfloat
        r.timestamp = timestamp
        r.keytype = keytype
        r.valtype = valtype
        r.comment = comment
        r.valid = valid
        r.property = property
        return r
    }
}

annotation class model(val options: Array<String> = [], val parent: KClass<*> = Null::class)

typealias ModelOptionStore = MutableMap<KClass<*>, ModelOption?>

// 缓存所有查找过的模型，非模型则对应的modeloption为null
private var _models: ModelOptionStore = mutableMapOf()

// 查找类对应的模型信息
fun FindModel(clz: KClass<*>): ModelOption? {
    if (_models.contains(clz)) {
        return _models[clz]
    }

    var mp: ModelOption? = null
    clz.annotations.forEach { ann ->
        if (ann is model) {
            mp = ModelOption()
            ann.options.forEach {
                when (it) {
                    hidden -> mp!!.hidden = true
                    auth -> mp!!.auth = true
                    enumm -> mp!!.enum = true
                    constant -> mp!!.constant = true
                }
            }

            if (ann.parent != Null::class)
                mp!!.parent = ann.parent
        }
    }

    _models[clz] = mp
    return mp
}

// 是否需要登陆验证
fun IsNeedAuth(mdl: Any?): Boolean {
    if (mdl == null)
        return false
    val clz = mdl.javaClass.kotlin
    val mp = _models[clz]
    return mp?.auth ?: false
}

annotation class string(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

annotation class integer(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

annotation class decimal(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

annotation class boolean(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

annotation class timestamp(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

annotation class json(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

annotation class map(
    val id: Int,
    val keyType: KClass<*>,
    val valueType: KClass<*>,
    val options: Array<String>,
    val comment: String = "",
)

annotation class array(
    val id: Int,
    val valueType: KClass<*>,
    val options: Array<String>,
    val comment: String = "",
)

annotation class enumerate(
    val id: Int,
    val type: KClass<*>,
    val options: Array<String>,
    val comment: String = "",
)

annotation class filter(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

annotation class type(
    val id: Int,
    val type: KClass<*>,
    val options: Array<String>,
    val comment: String = "",
)

annotation class intfloat(
    val id: Int,
    val scale: Float,
    val options: Array<String>,
    val comment: String = "",
)

// 百分数格式化至 0-10000之间
annotation class percentage(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

// 钱格式化到 0-100 之间
annotation class money(
    val id: Int,
    val options: Array<String>,
    val comment: String = "",
)

val FIELDS_MAX = 100

typealias FieldOptionStore = MutableMap<KClass<*>, MutableMap<String, FieldOption>>

private val _fieldoptions: FieldOptionStore = mutableMapOf()

fun GetAllFields(proto: KClass<*>): MutableMap<String, FieldOption>? {
    // 参见 FindAction
    var fps = _fieldoptions[proto]
    if (fps != null)
        return fps

    fps = mutableMapOf()
    proto.declaredMemberProperties.forEach { prop ->
        prop.annotations.forEach { ann ->
            var fp: FieldOption? = null
            when (ann) {
                is string -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.string = true
                }
                is integer -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.integer = true
                }
                is decimal -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.decimal = true
                }
                is boolean -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.boolean = true
                }
                is json -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.json = true
                }
                is map -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.map = true
                    fp.keytype = ann.keyType
                    fp.valtype = ann.valueType
                }
                is array -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.array = true
                    fp.valtype = ann.valueType
                }
                is enumerate -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.enum = true
                    fp.valtype = ann.type
                }
                is filter -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.filter = true
                }
                is type -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.valtype = ann.type
                }
                is timestamp -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.timestamp = true
                }
                is intfloat -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.intfloat = ann.scale
                }
                is percentage -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.intfloat = IntFloat.SCALE_PERCENTAGE
                }
                is money -> {
                    fp = DefineField(ann.id, prop, ann.options, ann.comment)
                    fp.intfloat = IntFloat.SCALE_MONEY
                }
            }
            if (fp != null)
                fps[prop.name] = fp
        }
    }

    // 处理一下父类的id偏移
    val mp = FindModel(proto)
    if (mp?.parent != null) {
        val pfps = GetAllFields(mp.parent!!)
        pfps!!.forEach { name, fp ->
            val nfp = fp.clone()
            nfp.id *= FIELDS_MAX // 每一次继承，id后面x100，即一个类参数最大数量不能超过100
            fps[name] = nfp
        }
    }

    _fieldoptions[proto] = fps
    return fps
}

private fun DefineField(id: Int, prop: KProperty<*>, options: Array<String>, comment: String): FieldOption {
    val r = FieldOption()
    r.id = id
    r.optional = optional in options
    r.input = input in options
    r.output = output in options
    r.comment = comment
    r.property = prop
    return r
}

// 收集model的输出
fun Output(mdl: Any?): MutableMap<String, Any?>? {
    if (mdl == null)
        return null

    val clz = mdl.javaClass.kotlin
    val fps = GetAllFields(clz)!!
    val r = mutableMapOf<String, Any?>()
    for ((fk, fp) in fps) {
        if (!fp.output)
            continue
        val v = fp.property.getter.call(mdl)
        if (fp.valtype != null) {
            if (fp.array) {
                val arr = mutableListOf<Any?>()
                // 通用类型，则直接可以输出
                if (v == null) {
                    // 处理val==null的情况
                } else if (fp.valtype == String::class) {
                    (v as List<*>).forEach {
                        arr.add(asString(it))
                    }
                } else if (TypeIsInteger(fp.valtype!!)) {
                    (v as List<*>).forEach {
                        arr.add(toInteger(it))
                    }
                } else if (TypeIsDecimal(fp.valtype!!)) {
                    (v as List<*>).forEach {
                        arr.add(toDecimal(it))
                    }
                } else if (fp.valtype == Boolean::class) {
                    (v as List<*>).forEach {
                        arr.add(toBoolean(it))
                    }
                } else {
                    (v as List<*>).forEach {
                        arr.add(Output(it))
                    }
                }
                r[fk] = arr
            } else if (fp.map) {
                val map = mutableMapOf<Any, Any?>()
                if (v == null) {
                    // pass
                } else if (fp.valtype == String::class) {
                    (v as Map<*, *>).forEach {
                        map[it.key!!] = asString(it.value)
                    }
                } else if (TypeIsInteger(fp.valtype!!)) {
                    (v as Map<*, *>).forEach {
                        map[it.key!!] = toInteger(it.value)
                    }
                } else if (TypeIsDecimal(fp.valtype!!)) {
                    (v as Map<*, *>).forEach {
                        map[it.key!!] = toDecimal(it.value)
                    }
                } else if (fp.valtype == Boolean::class) {
                    (v as Map<*, *>).forEach {
                        map[it.key!!] = toBoolean(it.value)
                    }
                } else {
                    (v as Map<*, *>).forEach {
                        map[it.key!!] = Output(it.value)
                    }
                }
                r[fk] = map
            } else if (fp.enum) {
                r[fk] = EnumValue(v, -1)
            } else {
                r[fk] = Output(v)
            }
        } else {
            if (v == null) {
                r[fk] = null
            } else if (fp.string) {
                r[fk] = asString(v)
            } else if (fp.integer) {
                r[fk] = toInteger(v)
            } else if (fp.decimal) {
                r[fk] = toDecimal(v)
            } else if (fp.boolean) {
                r[fk] = toBoolean(v)
            } else if (fp.enum) {
                r[fk] = EnumValue(v.javaClass, -1)
            } else if (fp.filter) {
                r[fk] = v.toString()
            } else if (fp.intfloat != null) {
                r[fk] = IntFloat.From(v, fp.intfloat!!).origin
            } else if (fp.timestamp) {
                r[fk] = (v as DateTime).timestamp
            } else {
                if (FindModel(v.javaClass.kotlin) != null) {
                    r[fk] = Output(v)
                } else {
                    r[fk] = toJsonObject(v)?.flat()
                }
            }
        }
    }

    return r
}