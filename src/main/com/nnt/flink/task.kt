package com.nnt.flink

import com.nnt.core.Jsonobj
import com.nnt.core.logger
import com.nnt.manager.App
import com.nnt.task.Task
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator

open class Task : Task() {

    val subtasks = mutableListOf<SubTask>()

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false
        if (cfg.has("subtask")) {
            for (e in cfg["subtask"]) {
                val t = App.shared.instanceEntry(e.asText())
                if (t is SubTask) {
                    subtasks.add(t)
                } else {
                    logger.fatal("${id}@task 设置的 ${e.asText()} 没有继承于 SubTask")
                    return false
                }
            }
        }
        return true
    }

    override fun start() {
        super.start()
    }
}

abstract class SubTask {

    // 应用源处理子任务
    abstract fun <T> apply(source: SingleOutputStreamOperator<T>)

}
