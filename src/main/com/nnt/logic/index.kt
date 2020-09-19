package com.nnt.logic

import com.nnt.logic.manager.App

class Index {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            // 加载配置
            App.LoadConfig()

            // 启动程序
            val app = App()
            app.start()
        }
    }
}