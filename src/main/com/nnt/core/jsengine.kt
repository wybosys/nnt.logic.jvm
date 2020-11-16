package com.nnt.core

import com.eclipsesource.v8.*

class JsEngine {

    private val _v8 = V8.createV8Runtime()

    protected fun finalize() {
        _v8.release()
    }

    fun get(key: String): V8Object? {
        return _v8.getObject(key)
    }

    fun eval(script: String) {
        _v8.executeScript(script)
    }

    fun array(vararg p: Any?): V8Array {
        val r = V8Array(_v8)
        p.forEach {
            if (it == null) {
                r.pushNull()
            } else {
                r.push(it)
            }
        }
        return r
    }

    fun map(ps: Map<*, *>): V8Object {
        val r = V8Object(_v8)
        ps.forEach { k, v ->
            if (v == null) {
                r.addNull(k.toString())
            } else if (v is Number) {
                r.add(k.toString(), v.toInt())
            } else if (v is String) {
                r.add(k.toString(), v)
            } else if (v is Boolean) {
                r.add(k.toString(), v)
            }
        }
        return r
    }

    fun callback(cb: (receiver: V8Object, parameters: V8Array) -> Unit): V8Function {
        return V8Function(_v8, object : JavaCallback {
            override fun invoke(receiver: V8Object, parameters: V8Array): Any {
                cb(receiver, parameters)
                return V8Object(_v8)
            }
        })
    }
}