package com.nnt.manager

import com.nnt.config.NodeIsEnable
import com.nnt.core.GetExceptionMessage
import com.nnt.core.JsonObject
import com.nnt.core.logger
import com.nnt.logger.AbstractLogger
import com.nnt.logger.Filter

enum class LOGTYPE {
    LOG,
    WARN,
    INFO,
    ERROR,
    FATAL,
    EXCEPTION
}

private val loggers = mutableListOf<AbstractLogger>()

fun output(msg: String, filter: String, typ: LOGTYPE) {
    loggers.forEach() {
        if (!it.isAllow(filter))
            return
        when (typ) {
            LOGTYPE.LOG -> {
                it.log(msg)
            }
            LOGTYPE.INFO -> {
                it.info(msg)
            }
            LOGTYPE.WARN -> {
                it.warn(msg)
            }
            LOGTYPE.EXCEPTION -> {
                it.exception(msg)
            }
            LOGTYPE.ERROR -> {
                it.error(msg)
            }
            LOGTYPE.FATAL -> {
                it.fatal(msg)
            }
        }
    }
}

object Loggers {

    init {
        logger.log = {
            if (loggers.size > 0) {
                output(it, Filter.LOG, LOGTYPE.LOG)
            } else {
                println(it)
            }
        }

        logger.warn = {
            if (loggers.size > 0) {
                output(it, Filter.WARN, LOGTYPE.WARN)
            } else {
                println(it)
            }
        }

        logger.info = {
            if (loggers.size > 0) {
                output(it, Filter.INFO, LOGTYPE.INFO)
            } else {
                println(it)
            }
        }

        logger.error = {
            if (loggers.size > 0) {
                output(it, Filter.ERROR, LOGTYPE.ERROR)
            } else {
                println(it)
            }
        }

        logger.fatal = {
            if (loggers.size > 0) {
                output(it, Filter.FATAL, LOGTYPE.FATAL)
            } else {
                println(it)
            }
        }

        logger.exception = {
            if (loggers.size > 0) {
                val msg = GetExceptionMessage(it)
                output(msg, Filter.EXCEPTION, LOGTYPE.EXCEPTION)
            } else {
                println(it)
            }
        }
    }

    fun Start(cfg: JsonObject) {
        if (!cfg.isArray) {
            logger.fatal("logger的配置不是数组")
            return
        }

        cfg.forEach { it ->
            if (!NodeIsEnable(it))
                return@forEach

            val cfg_entry = it["entry"]!!.asString()
            val t = App.shared.instanceEntry(cfg_entry) as AbstractLogger?
            if (t == null) {
                println("${cfg_entry} 实例化失败")
            }

            val id = it["id"]!!.asString()
            if (t!!.config(it)) {
                loggers.add(t)
                println("输出log至 ${id}")
            } else {
                println("${id} 配置失败")
            }
        }
    }

    fun Stop() {
        loggers.clear()
    }
}
