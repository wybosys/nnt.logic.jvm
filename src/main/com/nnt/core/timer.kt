package com.nnt.core

import com.nnt.signals.Object
import com.nnt.signals.kSignalAction
import com.nnt.signals.kSignalDone

abstract class CTimer(_interval: Seconds = 1.0, _count: Int = -1) : Object() {

    // tick 的次数
    var count: Int = _count

    // 间隔 s
    var interval: Seconds = _interval

    // timer 的附加数据
    var xdata: Any? = null

    // 当前激发的次数
    protected var _firedCount: Int = 0

    val firedCount: Int get() = _firedCount

    // 每一次激发的增量
    protected var _deltaFired: Int = 1

    val deltaFired: Int get() = _deltaFired

    protected override fun _initSignals() {
        super._initSignals()
        _signals!!.register(kSignalAction)
        _signals!!.register(kSignalDone)
    }

    // 已经过去了的时间
    val pastTime: Seconds get() = _firedCount * interval

    // 当前的逻辑时间戳
    var currentTime: UnixTimestamp = 0

    // 启动定时器
    abstract fun start()

    // 停止定时器
    abstract fun stop()

    // 是否正在运行
    abstract fun isRunning(): Boolean

    fun oneTick(@SuppressWarnings("UnusedParameters") delta: Int = 0) {
        _deltaFired = 0
        signals.emit(kSignalAction)
    }
}
