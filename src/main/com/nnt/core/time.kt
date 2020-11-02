package com.nnt.core

import kotlinx.coroutines.*

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