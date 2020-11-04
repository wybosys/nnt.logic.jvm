package com.nnt.server.parser

import com.nnt.core.FieldOption
import com.nnt.core.GetAllFields
import com.nnt.core.STATUS
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

    override fun decodeField(fp: FieldOption, value: Any?, input: Boolean, output: Boolean): Any {
        TODO("Not yet implemented")
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