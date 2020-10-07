package com.nnt

import com.nnt.config.Apollo
import com.nnt.manager.App
import com.nnt.signals.kSignalChanged

class Index {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            // 绑定启动参数
            App.args = args

            // 加载配置
            App.LoadConfig()

            // 启动程序
            val app = App()
            app.start()

            // 配置变化需要重启
            if (Apollo.enabled) {
                Apollo.signals.connect(kSignalChanged) {
                    // 重新加载配置
                    App.LoadConfig()

                    // 重启应用
                    app.stop()
                    
                    // 重新启动
                    app.start()
                }
            }

            System.`in`.read()
        }
    }
}