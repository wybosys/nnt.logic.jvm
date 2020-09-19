package com.nnt.logic.manager

open class App {

    init {
        _shared = this
    }

    fun start() {

    }

    companion object {

        private var _shared: App? = null

        val shared get() = _shared!!

        // 加载程序配置
        fun LoadConfig(appcfg: String = "~/app.json", devcfg: String = "~/devvops.json") {

        }
    }
}