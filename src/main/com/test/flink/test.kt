package com.test.flink

import com.nnt.flink.Task
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

class Test : Task() {

    override fun start() {
        super.start()

        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        env.execute(id)
    }
}
