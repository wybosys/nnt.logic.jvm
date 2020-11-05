package com.nnt.server.rest

import com.nnt.core.toJsonObject
import io.vertx.core.buffer.Buffer

fun ParseContentToParams(buf: Buffer, ct: String): Map<String, Any?> {
    val r = mutableMapOf<String, Any?>()

    when (ct) {
        "application/json" -> {
            val jsobj = toJsonObject(buf.toString())
            if (jsobj != null && jsobj.isMap) {
                (jsobj.flat() as Map<*, *>).forEach {
                    r[it.key.toString()] = it.value
                }
            }
        }
    }

    return r
}