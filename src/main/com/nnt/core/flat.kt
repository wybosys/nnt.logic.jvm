package com.nnt.core

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

typealias KeywordPalette = Map<String, String>

fun flat(obj: Any?, kws: KeywordPalette = KEYWORDS_FLAT): Any? {
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

    val r = mutableMapOf<String, Any?>()
    obj.javaClass.kotlin.memberProperties.forEach {
        if (it.visibility != KVisibility.PUBLIC)
            return@forEach
        if (it.name.startsWith("_"))
            return@forEach

        val v = it.getter.call(obj)
        val name = kws[it.name] ?: it.name
        if (v is KClass<*>)
            r[name] = v
        else
            r[name] = flat(v)
    }
    return r
}

fun flat(clz: KClass<*>, kws: KeywordPalette = KEYWORDS_FLAT): Map<String, Any?> {
    val r = mutableMapOf<String, Any?>()
    clz.companionObject?.memberProperties?.forEach {
        if (it.visibility != KVisibility.PUBLIC)
            return@forEach
        if (it.name.startsWith("_"))
            return@forEach

        val v = it.getter.call(clz.companionObjectInstance)
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
    "super_" to "super",
    "this_" to "this",
    "fun_" to "fun",
    "it_" to "it"
)
