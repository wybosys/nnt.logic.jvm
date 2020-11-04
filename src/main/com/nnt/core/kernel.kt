package com.nnt.core

// 定义秒的类型
typealias Seconds = Float

// 框架中基本小数定义为double
typealias Decimal = Double
typealias Number = Double

// 定义整数
typealias Integer = Long

interface INumber {
    fun toNumber(): Decimal
}

fun toDouble(o: Any?, def: Double = 0.0): Double {
    if (o == null)
        return def
    if (o is Number)
        return o.toDouble()
    if (o is String) {
        val r = o.toDoubleOrNull()
        return r ?: def
    }
    if (o is INumber)
        return o.toNumber()
    return def
}

fun toInt(o: Any?, def: Integer = 0): Integer {
    if (o == null)
        return def
    if (o is Number)
        return o.toLong()
    if (o is String) {
        val r = o.toLongOrNull()
        return r ?: def
    }
    if (o is INumber)
        return o.toNumber().toLong()
    return def
}

fun toNumber(o: Any?, def: Number = 0.0): Number {
    return toDouble(o, def)
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