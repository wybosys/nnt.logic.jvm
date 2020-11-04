package com.nnt.core

import kotlin.reflect.KClass

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
    var parent: Any? = null

}

interface FieldValidProc {

    // 可以附加一个错误码
    var status: STATUS

    // 当返回false时，即为验证失败，此时上层可以通过获取status来返回特定的错误状态码
    fun valid(inp: Any): Boolean
}

class FieldOption {

    // 唯一序号，后续类似pb的协议会使用id来做数据版本兼容
    var id: Int = 0;

    // 可选
    var optional: Boolean = false

    // 读取控制
    var input: Boolean = false
    var output: Boolean = false

    // 类型标签
    var array: Boolean = false
    var map: Boolean = false
    var multimap: Boolean = false
    var string: Boolean = false
    var integer: Boolean = false
    var double: Boolean = false
    var number: Boolean = false
    var boolean: Boolean = false
    var enum: Boolean = false
    var file: Boolean = false
    var json: Boolean = false
    var filter: Boolean = false
    var intfloat: Int? = null

    // 关联类型
    var keytype: Any? = null
    var valtype: Any? = null

    var comment: String = ""; // 注释

    // 有效性检查函数
    var valid: FieldValidProc? = null
}

annotation class model(val options: Array<String> = [])

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

annotation class type(
    val id: Int,
    val type: KClass<*>,
    val options: Array<String>,
    val comment: String = "",
)

// 收集model的输出
fun Output(mdl: Any?): MutableMap<String, Any?>? {
    if (mdl == null)
        return null

    return null
}