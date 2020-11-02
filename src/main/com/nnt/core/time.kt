package com.nnt.core

import kotlinx.coroutines.*
import java.time.LocalDateTime

suspend fun Sleep(seconds: Seconds) {
    delay((seconds * 1000).toLong())
}

abstract class DelayHandler(job: Job) {
    protected var _job = job
}

private class _DelayHandler(job: Job) : DelayHandler(job) {
    val job: Job get() = _job
}

fun Delay(seconds: Seconds, proc: () -> Unit): DelayHandler {
    val job = GlobalScope.launch {
        Sleep(seconds)
        if (isActive) {
            proc()
        }
    }
    return _DelayHandler(job)
}

fun CancelDelay(hdl: DelayHandler) {
    val h = hdl as _DelayHandler
    h.job.cancel()
}

abstract class RepeatHandler(job: Job) {
    protected var _job = job
}

private class _RepeatHandler(job: Job) : RepeatHandler(job) {
    val job: Job get() = _job
}

fun Repeat(seconds: Seconds, proc: () -> Unit): RepeatHandler {
    val job = GlobalScope.launch {
        while (isActive) {
            Sleep(seconds)
            if (isActive) {
                proc()
            }
        }
    }
    return _RepeatHandler(job)
}

fun CancelRepeat(hdl: RepeatHandler) {
    val h = hdl as _RepeatHandler
    h.job.cancel()
}

class Timeout(time: Seconds, proc: () -> Unit, autostart: Boolean = true) {

    private val _time = time
    private val _proc = proc
    private val _as = autostart

    private var _hdl: DelayHandler? = null

    init {
        if (_as)
            start()
    }

    fun start(): Boolean {
        if (_hdl != null) {
            logger.fatal("定时器已经开始执行")
            return false
        }

        _hdl = Delay(_time) {
            _proc()
            _hdl = null
        }

        return true
    }

    fun stop() {
        if (_hdl == null)
            return
        CancelDelay(_hdl!!)
        _hdl = null
    }
}

class Interval(time: Seconds, proc: () -> Unit, autostart: Boolean = true) {

    private val _time = time
    private val _proc = proc
    private val _as = autostart

    private var _hdl: RepeatHandler? = null

    init {
        if (_as)
            start()
    }

    fun start(): Boolean {
        if (_hdl != null) {
            logger.fatal("定时器已经开始执行")
            return false
        }

        _hdl = Repeat(_time) {
            _proc()
        }

        return true
    }

    fun stop() {
        if (_hdl == null)
            return
        CancelRepeat(_hdl!!)
        _hdl = null
    }
}

fun InstanceDate(): LocalDateTime {
    return LocalDateTime.now()
}

typealias Timestamp = Long

class DateTime {

    constructor() : this(Current()) {
        // pass
    }

    constructor(ts: Timestamp) {
        timestamp = ts
    }

    // 未来
    fun future(ts: Long): DateTime {
        timestamp += ts;
        return this
    }

    // 过去
    fun past(ts: Long): DateTime {
        timestamp -= ts
        return this
    }

    var timestamp: Timestamp = 0

    companion object {

        val MINUTE = 60
        val MINUTE_2 = 120
        val MINUTE_3 = 180
        val MINUTE_4 = 240
        val MINUTE_5 = 300
        val MINUTE_15 = 900
        val MINUTE_30 = 1800
        val HOUR = 3600
        val HOUR_2 = 7200
        val HOUR_6 = 21600
        val HOUR_12 = 43200
        val DAY = 86400
        val WEEK = 604800
        val MONTH = 2592000
        val YEAR = 31104000

        fun Now(): Double {
            val ts = System.currentTimeMillis()
            return ts / 1000.0
        }

        fun Current(): Timestamp {
            val ts = System.currentTimeMillis()
            return ts / 1000
        }

        fun Pass(): Timestamp {
            return DateTime.Current() - __time_started
        }
    }
}

private var __time_started = DateTime.Current()

suspend fun Retry(cond: () -> Boolean, proc: () -> Unit, interval: Seconds = 1f, delta: Seconds = 2f) {
    if (!cond()) {
        Sleep(interval)
        Retry(cond, proc, interval + delta, delta)
    } else {
        proc()
    }
}