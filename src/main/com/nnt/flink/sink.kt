package com.nnt.flink

import com.nnt.core.logger
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction

abstract class SafeSinkFunction<T> : RichSinkFunction<T>() {

    override fun invoke(value: T, context: SinkFunction.Context<*>?) {
        try {
            super.invoke(value, context)
        } catch (err: Throwable) {
            logger.exception(err)
        }
    }

    override fun invoke(value: T) {
        // super.invoke(value)
        sink(value)
    }

    abstract fun sink(value: T)
}