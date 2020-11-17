package com.nnt.thirds.dust

import com.nnt.core.JsCallback
import com.nnt.core.JsEngine
import com.nnt.core.JsObject
import com.nnt.core.logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DustCompiler {

    private val _jseng = JsEngine()
    private val _jsdust: JsObject
    private val _compiled = mutableMapOf<String, String>()

    init {
        _jseng.eval(SCRIPT_DUSTJS)
        _jsdust = _jseng.get("dust")
    }

    fun clear() {
        _compiled.clear()
    }

    fun compiled(idr: String): Boolean {
        return _compiled.containsKey(idr)
    }

    fun compile(src: String, idr: String): Boolean {
        try {
            val res = _jsdust.invoke("compile", src, idr)
            _compiled[idr] = res as String
            return true
        } catch (err: Throwable) {
            logger.exception(err)
            return false
        }
    }

    suspend fun render(idr: String, params: Map<*, *>): String = suspendCoroutine { cont ->
        if (!_compiled.containsKey(idr)) {
            cont.resume("")
            return@suspendCoroutine
        }

        try {
            val tpl = _compiled[idr]
            _jsdust.invoke("loadSource", tpl)
            _jsdust.invoke("render",
                idr, params,
                object : JsCallback {
                    override fun invoke(err: Error?, params: List<Any?>) {
                        if (err != null) {
                            cont.resumeWithException(err)
                        } else {
                            cont.resume(params[0] as String)
                        }
                    }
                }
            )
        } catch (err: Throwable) {
            logger.exception(err)
            cont.resume("")
            return@suspendCoroutine
        }
    }
}