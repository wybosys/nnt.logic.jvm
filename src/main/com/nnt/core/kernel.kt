package com.nnt.core

// 定义秒的类型
typealias Seconds = Double

// 框架中基本小数定义为double
typealias Decimal = Double

// 同理实数定义为double
typealias Real = Double

// 定义整数
typealias Integer = Long

// 转换成数值类型
interface IValue {
    fun valueOf(): Any
}

fun toValue(v: Array<*>): Array<Any?> {
    return v.map {
        if (it is IValue) it.valueOf() else it
    }.toTypedArray()
}

fun toDecimal(o: Any?, def: Real = 0.0): Double {
    if (o == null)
        return def
    if (o is Number)
        return o.toDouble()
    if (o is String) {
        val r = o.toDoubleOrNull()
        return if (r === null) def else r
    }
    return def
}

fun toInt(o: Any?, def: Int = 0): Int {
    if (o == null)
        return def
    if (o is Number)
        return o.toInt()
    if (o is String) {
        val r = o.toIntOrNull()
        return if (r === null) def else r
    }
    return def
}

fun toInteger(o: Any?, def: Integer = 0): Integer {
    if (o == null)
        return def
    if (o is Number)
        return o.toLong()
    if (o is String) {
        val r = o.toLongOrNull()
        return if (r === null) def else r
    }
    return def
}

fun toBoolean(o: Any?): Boolean {
    if (o is String)
        return o != "false"
    if (o is Number)
        return o.toInt() > 0
    if (o is Boolean)
        return o
    return o != null
}

fun asString(o: Any?, def: String = ""): String {
    if (o == null)
        return def
    if (o is String)
        return o
    return o.toString()
}

fun <T> use(o: T, proc: (self: T) -> Unit): T {
    proc(o)
    return o
}

inline fun <reified T> ava(o: Any?, r: T): T {
    if (o == null || o !is T)
        return r
    return o
}
