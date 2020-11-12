package com.nnt

import com.nnt.core.URI
import com.nnt.manager.App
import com.nnt.manager.Cli

class Job {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            // 绑定启动参数
            App.args = args

            // 配置文件路径
            val appcfg = URI("bundle://job.json")
            val devcfg = URI("bundle://devops.json")

            // 加载配置
            App.LoadConfig(appcfg, devcfg)

            // 启动程序
            val app = App()
            app.start()

            Cli.Run()
        }
    }
}