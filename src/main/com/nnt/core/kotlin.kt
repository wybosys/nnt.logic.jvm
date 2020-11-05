package com.nnt.core

import kotlin.reflect.KType
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
            Short::class.java -> {
                if (v is Number)
                    return v.toShort()
            }
            Long::class.java -> {
                if (v is Number)
                    return v.toLong()
            }
        }
    }

    return v
}