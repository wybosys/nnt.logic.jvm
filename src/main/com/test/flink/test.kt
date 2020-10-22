package com.test.flink

import com.nnt.flink.Task
import com.nnt.manager.Servers
import com.nnt.server.Kafka

class Test : Task() {

    override fun start() {
        super.start()

        //val env = StreamExecutionEnvironment.getExecutionEnvironment()

        //env.execute(id)

        val k = Servers.Find("kafka") as Kafka
        val cli = k.instanceClient()
        cli.open("xaas-logs")
        cli.subscribe { msg, _ ->
            println(msg)
        }
        cli.produce("hello")
    }
}
