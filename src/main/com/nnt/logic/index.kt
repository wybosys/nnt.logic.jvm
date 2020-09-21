package com.nnt.logic

import com.nnt.logic.manager.App
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Index {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            App.args = args

            // 加载配置
            App.LoadConfig()

            // 启动程序
            val app = App()
            GlobalScope.launch {
                app.start()
            }

            System.`in`.read()
        }
    }
}