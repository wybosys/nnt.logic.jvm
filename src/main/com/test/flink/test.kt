package com.test.flink

import com.nnt.flink.Task
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

class Test : Task() {

    override fun start() {
        super.start()

        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        env.parallelism = 1

        val ds = env.fromCollection(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
        ds.print()

        env.execute(id)
    }
}
