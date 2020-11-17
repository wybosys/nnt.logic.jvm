package com.nnt.core

import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

fun flat(obj: Any?): Any? {
    if (obj == null)
        return null
    if (obj is Number) {
        return obj
    }
    if (obj is String) {
        return obj
    }
    if (obj is Boolean) {
        return obj
    }
    if (obj is Array<*>) {
        return obj.map {
            flat(it)
        }
    }
    if (obj is Map<*, *>) {
        val r = mutableMapOf<Any, Any?>()
        obj.forEach { (k, v) ->
            r[flat(k)!!] = flat(v)
        }
        return r
    }

    val r = mutableMapOf<Any, Any?>()
    obj.javaClass.kotlin.memberProperties.forEach {
        if (it.visibility != KVisibility.PUBLIC)
            return@forEach
        if (it.name.startsWith("_"))
            return@forEach
        val v = it.getter.call(obj)
        r[it.name] = flat(v)
    }
    return r
}