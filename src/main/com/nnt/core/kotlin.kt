package com.nnt.core

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaType

// 基础对象间转换
fun ToType(v: Any?, typ: KType): Any? {
    if (v == null)
        return null

    if (v.javaClass != typ.javaType) {
        when (typ.javaType) {
            Int::class.java -> {
                if (v is Number)
                    return v.toInt()
            }
            Double::class.java -> {
                if (v is Number)
                    return v.toDouble()
            }
            Short::class.java -> {
                if (v is Number)
                    return v.toShort()
            }
            Long::class.java -> {
                if (v is Number)
                    return v.toLong()
            }
            java.lang.Boolean::class.java -> {
                if (v is Byte)
                    return v != 0
            }
            DateTime::class.java -> {
                if (v is Long)
                    return DateTime(v / 1000)
            }
            else -> {
                logger.fatal("ToType遇到未处理的类型 ${v.javaClass}")
            }
        }
    }

    return v
}

// 从enum值获得enum对象
fun <T : Any> ToEnum(clz: KClass<T>, v: String, def: T? = null): T? {
    val jclz = clz.java
    if (!jclz.isEnum)
        return def

    @Suppress("UNCHECKED_CAST")
    val consts = (jclz.enumConstants as Array<Enum<*>>)
    try {
        @Suppress("UNCHECKED_CAST")
        return consts.first {
            it.name == v
        } as T
    } catch (err: Throwable) {
        // pass
    }
    return def
}

fun <T : Any> ToEnum(clz: KClass<T>, v: Int, def: T? = null): T? {
    val jclz = clz.java
    if (!jclz.isEnum)
        return def

    try {
        val prop = clz.declaredMemberProperties.first {
            TypeIsInteger((it.returnType.javaType as Class<*>).kotlin)
        }

        @Suppress("UNCHECKED_CAST")
        val consts = (jclz.enumConstants as Array<Enum<*>>)
        try {
            @Suppress("UNCHECKED_CAST")
            return consts.first {
                prop.getter.call(it) == v
            } as T
        } catch (err: Throwable) {
            // pass
        }
    } catch (err: Throwable) {
    }

    return def
}

// 获得enum值
fun EnumValue(v: Any?, def: Int = 0): Int {
    if (v == null)
        return def

    val clz = v.javaClass.kotlin
    try {
        val prop = clz.declaredMemberProperties.first {
            TypeIsInteger((it.returnType.javaType as Class<*>).kotlin)
        }
        return prop.getter.call(v) as Int
    } catch (err: Throwable) {
        // pass
    }
    return def
}