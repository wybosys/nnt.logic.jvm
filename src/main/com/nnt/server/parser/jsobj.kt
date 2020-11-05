package com.nnt.server.parser

import com.nnt.core.*
import kotlin.reflect.KClass

class Jsobj : AbstractParser() {

    override fun checkInput(proto: KClass<*>, params: Map<String, *>): STATUS {
        val fps = GetAllFields(proto)
        if (fps == null)
            return STATUS.OK
        for ((name, fp) in fps) {
            if (!fp.input)
                continue
            val inp = params[name]
            if (fp.optional) {
                if (fp.valid != null && inp != null) {
                    val v = decodeField(fp, inp, true, false)
                    if (!fp.valid!!.valid(v))
                        return fp.valid!!.status
                }
                continue
            }
            if (inp == null)
                return STATUS.PARAMETER_NOT_MATCH
            // 判断是否合规
            if (fp.valid != null) {
                // 需要提前转换一下类型
                val v = decodeField(fp, inp, true, false)
                if (!fp.valid!!.valid(v))
                    return fp.valid!!.status
            }
        }
        return STATUS.OK
    }

    override fun decodeField(fp: FieldOption, _val: Any?, input: Boolean, output: Boolean): Any? {
        var value: Any? = _val
        if (fp.valtype != null) {
            if (fp.array) {
                val arr = mutableListOf<Any?>()
                if (value != null) {
                    if (fp.valtype == String::class) {
                        if (value is String) {
                            // 对于array，约定用，来分割
                            value = value.split(",")
                        }
                        if (fp.valtype == String::class) {
                            (value as List<*>).forEach {
                                arr.add(it?.toString())
                            }
                        } else if (TypeIsInteger(fp.valtype!!)) {
                            (value as List<*>).forEach {
                                arr.add(toInt(it))
                            }
                        } else if (TypeIsDecimal(fp.valtype!!)) {
                            (value as List<*>).forEach {
                                arr.add(toDouble(it))
                            }
                        } else if (fp.valtype == Boolean::class) {
                            (value as List<*>).forEach {
                                arr.add(toBoolean(it))
                            }
                        }
                    } else {
                        if (value is String)
                            value = toJsonObject(value)
                        if (value != null) {
                            if (value is Array<*>) {
                                val clz = fp.valtype!!
                                value.forEach {
                                    val t = clz.constructors.first().call()

                                    @Suppress("UNCHECKED_CAST")
                                    fill(t, it as Map<String, *>, input, output)

                                    arr.add(t)
                                }
                            } else {
                                logger.log("Array遇到了错误的数据 ${value}")
                            }
                        }
                    }
                }
                return arr
            } else if (fp.map) {
                var keyconv: (v: Any) -> Any = { it }
                if (TypeIsInteger(fp.keytype!!))
                    keyconv = { toInt(it) }
                else if (TypeIsDecimal(fp.keytype!!))
                    keyconv = { toDouble(it) }
                val map = mutableMapOf<Any, Any?>()
                if (fp.valtype == String::class) {
                    for ((ek, ev) in (value as Map<*, *>)) {
                        map[keyconv(ek!!)] = asString(ev)
                    }
                } else if (TypeIsInteger(fp.valtype!!)) {
                    for ((ek, ev) in (value as Map<*, *>)) {
                        map[keyconv(ek!!)] = toInt(ev)
                    }
                } else if (TypeIsDecimal(fp.valtype!!)) {
                    for ((ek, ev) in (value as Map<*, *>)) {
                        map[keyconv(ek!!)] = toDouble(ev)
                    }
                } else if (fp.valtype == Boolean::class) {
                    for ((ek, ev) in (value as Map<*, *>)) {
                        map[keyconv(ek!!)] = toBoolean(ev)
                    }
                } else {
                    val clz = fp.valtype!!
                    for ((ek, ev) in (value as Map<*, *>)) {
                        val t = clz.constructors.first().call()

                        @Suppress("UNCHECKED_CAST")
                        fill(t, ev as Map<String, *>, input, output)

                        map[keyconv(ek!!)] = t
                    }
                }
                return map
            } else if (fp.enum) {
                return toInt(value)
            } else {
                if (fp.valtype == String::class) {
                    // value = toJsonObject(value)
                }
                if (fp.valtype == Any::class)
                    return value
                val clz = fp.valtype!!

                val t = clz.constructors.first().call()

                @Suppress("UNCHECKED_CAST")
                fill(t, value as Map<String, *>, input, output)

                return t
            }
        } else {
            if (fp.string) {
                return asString(value)
            } else if (fp.integer) {
                return toInt(value)
            } else if (fp.double) {
                return toDouble(value)
            } else if (fp.boolean) {
                return toBoolean(value)
            } else if (fp.enum) {
                return toInt(value)
            } else if (fp.json) {
                // return toJsonObject(value)
                return null
            } else {
                return value
            }
        }
    }

    override fun fill(mdl: Any, params: Map<String, *>, input: Boolean, output: Boolean) {
        val clz = mdl.javaClass
        val proto = clz.kotlin

        val fps = GetAllFields(proto)
        if (fps == null)
            return
        for ((name, inp) in params) {
            val fp = fps[name]
            if (fp == null)
                continue
            if (input && !fp.input)
                continue
            if (output && !fp.output)
                continue
            val v = decodeField(fp, inp, input, output)

            // 设置到对象身上
            try {
                val setter = clz.getDeclaredMethod("set${name.capitalize()}")
                setter.invoke(mdl, v)
            } catch (err: Throwable) {
                // pass
            }
        }
    }

}