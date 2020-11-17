package com.nnt.core

import kotlin.reflect.KClass
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

    return flat(obj.javaClass.kotlin, obj)
}

fun flat(clz: KClass<*>, self: Any = clz, kws: Map<String, String> = KEYWORDS_FLAT): Map<String, Any?> {
    val r = mutableMapOf<String, Any?>()
    clz.memberProperties.forEach {
        if (it.visibility != KVisibility.PUBLIC)
            return@forEach
        if (it.name.startsWith("_"))
            return@forEach

        val v = it.getter.call(self)
        val name = kws[it.name] ?: it.name
        if (v is KClass<*>)
            r[name] = v
        else
            r[name] = flat(v)
    }
    return r
}

// 拍平用的关键字映射表
val KEYWORDS_FLAT = mapOf(
    "super" to "super_"
)
