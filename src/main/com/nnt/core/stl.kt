package com.nnt.core

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

typealias ArrayType<T> = List<T>

fun IsArray(o: Any): Boolean {
    if (o is List<*>)
        return true
    if (o is Array<*>)
        return true
    return false
}

fun ToArray(o: Any): List<*>? {
    if (o is List<*>)
        return o
    if (o is Array<*>)
        return o.toList()
    return null
}

fun IsInteger(o: Any): Boolean {
    return o is Int || o is Long || o is Short || o is BigInteger
}

fun TypeIsInteger(clz: KClass<*>): Boolean {
    return clz == Int::class || clz == Long::class || clz == Short::class || clz == BigInteger::class
}

fun IsDecimal(o: Any): Boolean {
    return o is Float || o is Double || o is BigDecimal
}

fun TypeIsDecimal(clz: KClass<*>): Boolean {
    return clz == Float::class || clz == Double::class || clz == BigDecimal::class
}

fun IsNumber(o: Any): Boolean {
    return IsInteger(o) || IsDecimal(o)
}

fun TypeIsReal(clz: KClass<*>): Boolean {
    return TypeIsInteger(clz) || TypeIsDecimal(clz)
}