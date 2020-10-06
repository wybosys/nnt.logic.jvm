package com.nnt.logic.manager

import com.nnt.logic.config.Apollo
import com.nnt.logic.core.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

open class App {

    init {
        _shared = this
    }

    // 启动服务
    suspend fun start() {
        val cfg = App.CurrentConfig!!
        if (cfg.has("logger")) {
            GlobalScope.async {
                Loggers.Start(cfg["logger"])
            }.await()
        }
        if (cfg.has("dbms")) {
            GlobalScope.async {
                Dbms.Start(cfg["dbms"])
            }.await()
        }
        if (cfg.has("server")) {
            GlobalScope.async {
                Servers.Start(cfg["server"])
            }.await()
        }
    }

    // 停止服务
    suspend fun stop() {
        GlobalScope.async {
            Loggers.Stop()
        }.await()
        GlobalScope.async {
            Dbms.Stop()
        }
        GlobalScope.async {
            Servers.Stop()
        }.await()
    }

    // 实例化对象
    fun instanceEntry(entry: String): Any? {
        val fnd = findEntry(entry)
        if (fnd != null)
            return fnd.constructors[0].newInstance()
        return null;
    }

    // 查找指定类型
    fun findEntry(entry: String): Class<*>? {
        try {
            return Class.forName(entry)
        } catch (e: Throwable) {
            print(e)
        }
        return null
    }

    companion object {

        init {
            // 运行各模块初始化函数
            com.nnt.logic.core.Init()
            com.nnt.logic.server.Init()
        }

        // 启动带的参数
        var args = arrayOf("")

        // 单件
        private var _shared: App? = null

        val shared get() = _shared!!

        // 当前app的配置参数
        var CurrentConfig: Jsonobj? = null

        // 加载程序配置
        fun LoadConfig(appcfg: URI = URI("bundle://app.json"), devcfg: URI = URI("bundle://devops.json")): Jsonobj? {
            // 读取配置信息
            if (!File(appcfg).exists()) {
                println("读取配置文件失败 ${appcfg}");
                return null
            }

            if (!File(devcfg).exists()) {
                println("读取DEVOPS配置文件失败 ${devcfg}");
                return null
            }

            // 通过配置文件启动服务
            val cfg = toJsonObject(File(appcfg).readText())

            // 处理输入参数
            Config.DEBUG = args.indexOf("--debug") != -1
            if (Config.DEBUG) {
                logger.log("debug模式启动")
            } else {
                Config.DEVELOP = args.indexOf("--develop") != -1
                if (Config.DEVELOP) {
                    logger.log("develop模式启动")
                } else {
                    Config.PUBLISH = args.indexOf("--publish") != -1
                    if (Config.PUBLISH)
                        logger.log("publish模式启动")
                }
            }
            Config.DISTRIBUTION = !IsDebug()
            if (Config.DISTRIBUTION) {
                logger.log("distribution模式启动")
            }
            Config.LOCAL = IsLocal()
            if (Config.LOCAL) {
                logger.log("LOCAL 环境")
            }
            Config.DEVOPS = IsDevops()
            if (Config.DEVOPS) {
                logger.log("DEVOPS 环境")
            }
            Config.DEVOPS_DEVELOP = IsDevopsDevelop()
            if (Config.DEVOPS_DEVELOP) {
                logger.log("DEVOPS DEVELOP 环境")
            }
            Config.DEVOPS_RELEASE = IsDevopsRelease()
            if (Config.DEVOPS_RELEASE) {
                logger.log("DEVOPS RELEASE 环境")
            }

            // 设置为当前参数
            CurrentConfig = cfg

            // 读取系统配置
            val c = cfg!!["config"]!!
            if (c.has("cache")) {
                Config.CACHE = URI(c["cache"].asText()!!)
            }

            if (!File(Config.CACHE).exists()) {
                File(Config.CACHE).mkdirs()
            }

            // 如果使用apollo，则从对应服务读取
            if (c.has("apollo")) {
                if (!Apollo.config(c["apollo"])) {
                    logger.error("apollo配置失败")
                    return null
                }
                Merge(CurrentConfig!!, Apollo.value())
            }

            return cfg
        }
    }
}