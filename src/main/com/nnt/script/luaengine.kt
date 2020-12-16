package com.nnt.script

import com.nnt.core.logger
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue

fun FromLv(v: LuaValue): Any? {
    when (v.type()) {
        LuaValue.TNIL -> {
            return null
        }
        LuaValue.TBOOLEAN -> {
            return v.toboolean()
        }
        LuaValue.TNUMBER -> {
            if (v.isint())
                return v.toint()
            return v.todouble()
        }
        LuaValue.TSTRING -> {
            return v.tostring().toString()
        }
        else -> {
            logger.error("遇到未知lua值类型 ${v.type()}")
        }
    }
    return null
}

fun ObjectGet(obj: LuaTable, vararg kps: String): Any? {
    val fnd = obj[kps[0]]
    if (fnd.isnil())
        return null
    val next = kps.drop(1)
    if (next.isEmpty())
        return FromLv(fnd)
    if (!fnd.istable())
        return null
    return ObjectGet(fnd.checktable(), *next.toTypedArray())
}

fun ObjectSet(obj: LuaTable, v: Any?, vararg kps: String): Boolean {
    if (kps.isEmpty())
        return false
    if (kps.size == 1) {
        if (v == null) {
            obj.set(kps[0], LuaValue.NIL)
        } else if (v is Number) {
            if (v is Float || v is Double) {
                obj.set(kps[0], v.toDouble())
            } else {
                obj.set(kps[0], v.toInt())
            }
        } else if (v is Boolean) {
            obj.set(kps[0], if (v) LuaValue.TRUE else LuaValue.FALSE)
        } else if (v is String) {
            obj.set(kps[0], v)
        } else {
            logger.error("lua设置不支持的数据 ${v}")
            return false
        }
        return true
    }

    val fnd = obj[kps[0]]
    if (fnd.isnil() || !fnd.istable())
        return false
    val next = kps.drop(1)
    return ObjectSet(fnd.checktable(), v, *next.toTypedArray())
}