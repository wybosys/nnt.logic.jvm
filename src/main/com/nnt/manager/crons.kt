package com.nnt.manager

import com.nnt.core.logger
import it.sauronsoftware.cron4j.Scheduler

abstract class CronTask {

    // 任务id
    var id: String = ""

    // 执行依赖的时间
    var time: String = ""

    // 主执行函数
    abstract fun main()

    // 启动
    fun start(): Boolean {
        if (_doStart()) {
            tasks.add(this)
            return true
        }

        return false
    }

    // 停止
    fun stop() {
        if (_job == null)
            return

        _job!!.stop()
        _job = null

        tasks.remove(this)
    }

    // 是否位于计划任务中
    val cronning: Boolean
        get() {
            return _job != null && _job!!.isStarted
        }

    private var _job: Scheduler? = null

    private fun _doStart(): Boolean {
        _job = Scheduler()
        _job!!.schedule(time, object : Runnable {
            override fun run() {
                try {
                    main()
                } catch (err: Exception) {
                    logger.exception(err.localizedMessage)
                }
            }
        })
        _job!!.start()
        return true
    }
}

// 普通任务
private val tasks = mutableSetOf<CronTask>();

// 生成配置 cron4j不支持秒级
// fun PerSecond(v: Int = 1): String {
//    return "*/$v * * * *";
//}

fun PerMinute(v: Int = 1): String {
    return "*/$v * * * *";
}

fun PerHour(v: Int = 1): String {
    return "0 */$v * * *";
}

fun PerDay(v: Int = 1): String {
    return "0 0 */$v * *";
}
