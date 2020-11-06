package com.nnt.server.parser

import com.nnt.core.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

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

    override fun decodeField(fp: FieldOption, value: Any?, input: Boolean, output: Boolean): Any? {
        var v: Any? = value
        if (fp.valtype != null) {
            if (fp.array) {
                val arr = mutableListOf<Any?>()
                if (v != null) {
                    if (fp.valtype == String::class) {
                        if (v is String) {
                            // 对于array，约定用，来分割
                            v = v.split(",")
                        }
                        if (fp.valtype == String::class) {
                            (v as List<*>).forEach {
                                arr.add(it?.toString())
                            }
                        } else if (TypeIsInteger(fp.valtype!!)) {
                            (v as List<*>).forEach {
                                arr.add(toInteger(it))
                            }
                        } else if (TypeIsDecimal(fp.valtype!!)) {
                            (v as List<*>).forEach {
                                arr.add(toDecimal(it))
                            }
                        } else if (fp.valtype == Boolean::class) {
                            (v as List<*>).forEach {
                                arr.add(toBoolean(it))
                            }
                        }
                    } else {
                        if (v is String)
                            v = toJsonObject(v)!!.flat()
                        if (v != null) {
                            if (v is Array<*>) {
                                val clz = fp.valtype!!
                                v.forEach {
                                    val t = clz.constructors.first().call()

                                    @Suppress("UNCHECKED_CAST")
                                    fill(t, it as Map<String, *>, input, output)

                                    arr.add(t)
                                }
                            } else {
                                logger.log("Array遇到了错误的数据 ${v}")
                            }
                        }
                    }
                }
                return arr
            } else if (fp.map) {
                var keyconv: (v: Any) -> Any = { it }
                if (TypeIsInteger(fp.keytype!!))
                    keyconv = { toInteger(it) }
                else if (TypeIsDecimal(fp.keytype!!))
                    keyconv = { toDecimal(it) }
                val map = mutableMapOf<Any, Any?>()
                if (fp.valtype == String::class) {
                    for ((ek, ev) in (v as Map<*, *>)) {
                        map[keyconv(ek!!)] = asString(ev)
                    }
                } else if (TypeIsInteger(fp.valtype!!)) {
                    for ((ek, ev) in (v as Map<*, *>)) {
                        map[keyconv(ek!!)] = toInteger(ev)
                    }
                } else if (TypeIsDecimal(fp.valtype!!)) {
                    for ((ek, ev) in (v as Map<*, *>)) {
                        map[keyconv(ek!!)] = toDecimal(ev)
                    }
                } else if (fp.valtype == Boolean::class) {
                    for ((ek, ev) in (v as Map<*, *>)) {
                        map[keyconv(ek!!)] = toBoolean(ev)
                    }
                } else {
                    val clz = fp.valtype!!
                    for ((ek, ev) in (v as Map<*, *>)) {
                        val t = clz.constructors.first().call()

                        @Suppress("UNCHECKED_CAST")
                        fill(t, ev as Map<String, *>, input, output)

                        map[keyconv(ek!!)] = t
                    }
                }
                return map
            } else if (fp.enum) {
                return EnumValue(v)
            } else {
                if (fp.valtype == String::class) {
                    v = toJsonObject(v)!!.flat()
                }
                if (fp.valtype == Any::class)
                    return v
                val clz = fp.valtype!!

                val t = clz.constructors.first().call()

                @Suppress("UNCHECKED_CAST")
                fill(t, v as Map<String, *>, input, output)

                return t
            }
        } else {
            if (fp.string) {
                return asString(v)
            } else if (fp.integer) {
                return toInteger(v)
            } else if (fp.decimal) {
                return toDecimal(v)
            } else if (fp.boolean) {
                return toBoolean(v)
            } else if (fp.enum) {
                return toInteger(v)
            } else if (fp.json) {
                return toJsonObject(v)
            } else {
                return v
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

            if (fp.property is KMutableProperty<*>) {
                // 绑定数据到对象
                val v = decodeField(fp, inp, input, output)
                (fp.property as KMutableProperty<*>).setter.call(mdl, ToType(v, fp.property.returnType))
            }
        }
    }

}