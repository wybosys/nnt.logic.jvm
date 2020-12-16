package com.nnt.script

class Javascript {

    private val _eng = JsEngine()

    // 运行js脚本
    fun eval(source: String) {
        _eng.eval(source)
    }
}