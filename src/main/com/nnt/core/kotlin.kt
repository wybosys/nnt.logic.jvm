package com.nnt.core

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaType

// 基础对象间转换
fun ToType(v: Any?, typ: KType): Any? {
    val kclzType = (typ.javaType as Class<*>).kotlin

    if (v == null) {
        if (!typ.isMarkedNullable) {
            // 不能为null，返回内置默认值
            when (kclzType) {
                Int::class -> {
                    return 0
                }
                Double::class -> {
                    return 0.0
                }
                Short::class -> {
                    return 0
                }
                Long::class -> {
                    return 0
                }
                String::class -> {
                    return ""
                }
                Boolean::class -> {
                    return false
                }
                DateTime::class -> {
                    return DateTime(0)
                }
                else -> {
                    logger.fatal("ToType遇到空值，需要提供默认值")
                }
            }
        }
        return null
    }

    if (v.javaClass != typ.javaType) {
        when (kclzType) {
            Int::class -> {
                if (v is Number)
                    return v.toInt()
            }
            Double::class -> {
                if (v is Number)
                    return v.toDouble()
            }
            Short::class -> {
                if (v is Number)
                    return v.toShort()
            }
            Long::class -> {
                if (v is Number)
                    return v.toLong()
            }
            Boolean::class -> {
                if (v is Byte)
                    return v != 0
            }
            DateTime::class -> {
                if (v is Long)
                    return DateTime(v / 1000)
            }
            else -> {
                if (kclzType.java.isEnum) {
                    return ToEnum(kclzType, toInt(v))
                }

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
        // pass
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

fun EnumToMap(clz: KClass<*>): Map<String, Any> {
    val r = mutableMapOf<String, Any>()
    clz.declaredMemberProperties.forEach {
        r[it.name] = it.getter.call(clz)!!
    }
    return r
}
