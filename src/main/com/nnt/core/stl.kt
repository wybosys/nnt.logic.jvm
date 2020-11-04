package com.nnt.core

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