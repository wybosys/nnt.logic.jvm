package com.nnt

import com.nnt.config.Apollo
import com.nnt.core.URI
import com.nnt.manager.App
import com.nnt.manager.Cli
import com.nnt.signals.kSignalChanged

class Dubbo {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            // 绑定启动参数
            App.args = args

            // 配置文件路径
            val appcfg = URI("bundle://dubbo.json")
            val devcfg = URI("bundle://devops.json")

            // 加载配置
            App.LoadConfig(appcfg, devcfg)

            // 启动程序
            val app = App()
            app.start()

            // 配置变化需要重启
            if (Apollo.enabled) {
                Apollo.signals.connect(kSignalChanged) {
                    // 重新加载配置
                    App.LoadConfig(appcfg, devcfg)

                    // 重启应用
                    app.stop()

                    // 重新启动
                    app.start()
                }
            }

            Cli.Run()
        }
    }
}