package com.nnt.thirds.dust

import com.eclipsesource.v8.V8Object
import com.nnt.core.JsEngine
import com.nnt.core.logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DustCompiler {

    private val _jseng = JsEngine()
    private val _jsdust: V8Object
    private val _compiled = mutableMapOf<String, String>()

    init {
        _jseng.eval(SCRIPT_DUSTJS)
        _jsdust = _jseng.get("dust")!!
    }

    fun clear() {
        _compiled.clear()
    }

    fun compiled(idr: String): Boolean {
        return _compiled.containsKey(idr)
    }

    fun compile(src: String, idr: String): Boolean {
        try {
            val res = _jsdust.executeStringFunction("compile", _jseng.array(src, idr))
            _compiled[idr] = res
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
            _jsdust.executeVoidFunction("loadSource", _jseng.array(tpl))
            _jsdust.executeVoidFunction("render",
                _jseng.array(idr, _jseng.map(params), _jseng.callback { _, parameters ->
                    if (parameters[0] != null) {
                        cont.resumeWithException(parameters[0] as Exception)
                    } else {
                        cont.resume(parameters[1] as String)
                    }
                }))
        } catch (err: Throwable) {
            logger.exception(err)
            cont.resume("")
            return@suspendCoroutine
        }
    }
}