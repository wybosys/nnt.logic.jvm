package com.nnt.core

import kotlinx.coroutines.delay
import org.joda.time.LocalDateTime
import org.joda.time.chrono.ISOChronology
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate

val TIMER_THREAD = "nnt-timer"

class TimestampRange(
    var from: UnixTimestamp = 0, // 开始
    var to: UnixTimestamp = 0, // 结束，比如小时 [0, 60)
) {

    operator fun plus(v: UnixTimestamp): TimestampRange {
        return TimestampRange(from + v, to + v)
    }

    operator fun minus(v: UnixTimestamp): TimestampRange {
        return TimestampRange(from - v, to - v)
    }
}

suspend fun <T : Number> Sleep(seconds: T) {
    delay((seconds.toDouble() * 1000).toLong())
}

abstract class DelayHandler(tmr: Timer) {
    protected var _tmr = tmr
}

private class _DelayHandler(tmr: Timer) : DelayHandler(tmr) {
    val tmr: Timer get() = _tmr
}

// 延迟执行
fun <T : Number> Delay(seconds: T, proc: () -> Unit): DelayHandler {
    val tmr = Timer()
    val ms = (seconds.toDouble() * 1000).toLong()
    tmr.schedule(ms) {
        proc()
    }
    return _DelayHandler(tmr)
}

fun CancelDelay(hdl: DelayHandler) {
    val h = hdl as _DelayHandler
    h.tmr.cancel()
}

abstract class RepeatHandler(tmr: Timer) {
    protected var _tmr = tmr
}

private class _RepeatHandler(tmr: Timer) : RepeatHandler(tmr) {
    val tmr: Timer get() = _tmr
}

// 按照时间段重复, 0时间不激活
fun <T : Number> Repeat(seconds: T, proc: () -> Unit): RepeatHandler {
    val tmr = Timer()
    val ms = (seconds.toDouble() * 1000).toLong()
    tmr.scheduleAtFixedRate(ms, ms) {
        proc()
    }
    return _RepeatHandler(tmr)
}

// 按照时间段重复, 0时间激活
fun <T : Number> Period(seconds: T, proc: () -> Unit): RepeatHandler {
    val tmr = Timer()
    val ms = (seconds.toDouble() * 1000).toLong()
    tmr.scheduleAtFixedRate(0, ms) {
        proc()
    }
    return _RepeatHandler(tmr)
}

fun CancelRepeat(hdl: RepeatHandler) {
    val h = hdl as _RepeatHandler
    h.tmr.cancel()
}

class Timeout<T : Number>(time: T, proc: () -> Unit, autostart: Boolean = true) {

    private val _time: Seconds = time.toDouble()
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

class Interval<T : Number>(time: T, proc: () -> Unit, autostart: Boolean = true) {

    private val _time: Seconds = time.toDouble()
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

// UNIX时间戳精确到秒
typealias UnixTimestamp = Long

// 精确到毫秒的时间戳
typealias FullTimestamp = Long

class DateTime : IValue, Cloneable {

    constructor() : this(Current()) {
        // pass
    }

    constructor(ts: UnixTimestamp) {
        timestamp = ts
    }

    constructor(dt: LocalDateTime) {
        _date = dt
        syncTimestamp()
    }

    protected fun syncTimestamp() {
        _timestamp = _date!!.toDateTime().millis / 1000
    }

    // 未来
    fun future(ts: Long): DateTime {
        timestamp += ts
        return this
    }

    // 过去
    fun past(ts: Long): DateTime {
        timestamp -= ts
        return this
    }

    private var _date: LocalDateTime? = null
    private var _timestamp: UnixTimestamp? = null

    var timestamp: UnixTimestamp
        get() {
            return _timestamp!!
        }
        set(value) {
            if (_timestamp == value)
                return
            _timestamp = value
            _date = LocalDateTime(value * 1000, ISOChronology.getInstance())
        }

    var year: Int
        get() {
            return _date!!.year
        }
        set(value) {
            _date = _date!!.withYear(value)
            syncTimestamp()
        }

    var hyear: Int
        get() {
            return year
        }
        set(value) {
            year = value
        }

    var month: Int
        get() {
            return _date!!.monthOfYear - 1
        }
        set(value) {
            _date = _date!!.withMonthOfYear(value + 1)
            syncTimestamp()
        }

    var hmonth: Int
        get() {
            return _date!!.monthOfYear
        }
        set(value) {
            _date = _date!!.withMonthOfYear(value)
            syncTimestamp()
        }

    var day: Int
        get() {
            return _date!!.dayOfMonth - 1
        }
        set(value) {
            _date = _date!!.withDayOfMonth(value + 1)
            syncTimestamp()
        }

    var hday: Int
        get() {
            return _date!!.dayOfMonth
        }
        set(value) {
            _date = _date!!.withDayOfMonth(value)
            syncTimestamp()
        }

    var hour: Int
        get() {
            return _date!!.hourOfDay
        }
        set(value) {
            _date = _date!!.withHourOfDay(value)
            syncTimestamp()
        }

    var minute: Int
        get() {
            return _date!!.minuteOfHour
        }
        set(value) {
            _date = _date!!.withMinuteOfHour(value)
            syncTimestamp()
        }

    var second: Int
        get() {
            return _date!!.secondOfMinute
        }
        set(value) {
            _date = _date!!.withSecondOfMinute(value)
            syncTimestamp()
        }

    val weekday: Int
        get() {
            return _date!!.dayOfWeek - 1
        }

    val hweekday: Int
        get() {
            return _date!!.dayOfWeek
        }

    /** 计算diff-year，根绝suffix的类型返回对应的类型 */
    fun <T : Number> dyears(up: Boolean = true, suffix: T? = null): Long {
        val v = Dyears(_timestamp!!, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dyears(up: Boolean = true, suffix: String): String {
        val v = Dyears(_timestamp!!, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dmonths(up: Boolean = true, suffix: T? = null): Long {
        val v = Dmonths(_timestamp!!, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dmonths(up: Boolean = true, suffix: String): String {
        val v = Dmonths(_timestamp!!, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> ddays(up: Boolean = true, suffix: T? = null): Long {
        val v = Ddays(_timestamp!!, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun ddays(up: Boolean = true, suffix: String): String {
        val v = Ddays(_timestamp!!, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dhours(up: Boolean = true, suffix: T? = null): Long {
        val v = Dhours(_timestamp!!, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dhours(up: Boolean = true, suffix: String): String {
        val v = Dhours(_timestamp!!, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dminutes(up: Boolean = true, suffix: T? = null): Long {
        val v = Dminutes(_timestamp!!, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dminutes(up: Boolean = true, suffix: String): String {
        val v = Dminutes(_timestamp!!, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dseconds(up: Boolean = true, suffix: T? = null): Long {
        val v = Dseconds(_timestamp!!, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dseconds(up: Boolean = true, suffix: String): String {
        val v = Dseconds(_timestamp!!, up)
        return if (v > 0) "$v$suffix" else ""
    }

    // 当前分钟的起始
    fun minuteRange(): TimestampRange {
        val from = this.timestamp - this.second
        return TimestampRange(
            from,
            from + 60 - 1 // 整数算在下一刻
        )
    }

    // 当前小时的起始
    fun hourRange(): TimestampRange {
        val from = this.timestamp - this.minute * DateTime.MINUTE - this.second
        return TimestampRange(
            from,
            from + DateTime.HOUR - 1
        )
    }

    // 一天的起始
    fun dayRange(): TimestampRange {
        val from = this.timestamp - this.hour * DateTime.HOUR - this.minute * DateTime.MINUTE - this.second
        return TimestampRange(
            from,
            from + DateTime.DAY - 1
        )
    }

    // 本周的起始
    fun weekRange(): TimestampRange {
        val from =
            this.timestamp - this.weekday * DateTime.DAY - this.hour * DateTime.HOUR - this.minute * DateTime.MINUTE - this.second
        return TimestampRange(
            from,
            from + DateTime.WEEK - 1
        )
    }

    // 本月的起始
    fun monthRange(): TimestampRange {
        val cur = LocalDateTime(_date!!.year, _date!!.monthOfYear, 0, 0, 0, 0)
        val next = cur.plusMonths(1)
        return TimestampRange(
            cur.toDateTime().millis / 1000,
            next.toDateTime().millis / 1000 - 1
        )
    }

    override fun toString(): String {
        return _date.toString()
    }

    override fun valueOf(): Any {
        return _timestamp!!
    }

    public override fun clone(): DateTime {
        return DateTime(_timestamp!!)
    }

    companion object {

        val MINUTE = 60L
        val MINUTE_2 = 120L
        val MINUTE_3 = 180L
        val MINUTE_4 = 240L
        val MINUTE_5 = 300L
        val MINUTE_15 = 900L
        val MINUTE_30 = 1800L
        val HOUR = 3600L
        val HOUR_2 = 7200L
        val HOUR_6 = 21600L
        val HOUR_12 = 43200L
        val DAY = 86400L
        val WEEK = 604800L
        val MONTH = 2592000L
        val YEAR = 31104000L

        fun Now(): Double {
            val ts = System.currentTimeMillis()
            return ts / 1000.0
        }

        fun Current(): UnixTimestamp {
            val ts = System.currentTimeMillis()
            return ts / 1000
        }

        fun Pass(): UnixTimestamp {
            return DateTime.Current() - __time_started
        }

        @Suppress("UNUSED_PARAMETER")
        fun Dyears(ts: UnixTimestamp, up: Boolean = true): Long {
            return Math.floorDiv(ts, YEAR)
        }

        fun Dmonths(ts: UnixTimestamp, up: Boolean = true): Long {
            var v: Long
            if (up) {
                v = ts % YEAR
                v = Math.floorDiv(v, MONTH)
            } else {
                v = Math.floorDiv(ts, MONTH)
            }
            return v
        }

        fun Ddays(ts: UnixTimestamp, up: Boolean = true): Long {
            var v: Long
            if (up) {
                v = ts % MONTH
                v = Math.floorDiv(v, DAY)
            } else {
                v = Math.floorDiv(ts, DAY)
            }
            return v
        }

        fun Dhours(ts: UnixTimestamp, up: Boolean = true): Long {
            var v: Long
            if (up) {
                v = ts % DAY
                v = Math.floorDiv(v, HOUR)
            } else {
                v = Math.floorDiv(ts, HOUR)
            }
            return v
        }

        fun Dminutes(ts: UnixTimestamp, up: Boolean = true): Long {
            var v: Long
            if (up) {
                v = ts % HOUR
                v = Math.floorDiv(v, MINUTE)
            } else {
                v = Math.floorDiv(ts, MINUTE)
            }
            return v
        }

        fun Dseconds(ts: UnixTimestamp, up: Boolean = true): Long {
            val v: Long
            if (up) {
                v = ts % MINUTE
            } else {
                v = ts
            }
            return v
        }

        // 标准的日期格式
        val FMT_NORMAL = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS")
        val FMT_NORMALH = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

        // ISO8601
        val FMT_ISO8601 = ISODateTimeFormat.dateTime()
        val FMT_IOS8601H = ISODateTimeFormat.dateTimeNoMillis()

        // 转换成时间戳
        fun ToUnixTimestamp(
            str: String,
            fmt: DateTimeFormatter,
            def: UnixTimestamp = 0,
        ): UnixTimestamp {
            try {
                return fmt.parseDateTime(str).millis / 1000
            } catch (err: Throwable) {
                return def
            }
        }
    }
}

private var __time_started = DateTime.Current()

suspend fun Retry(cond: () -> Boolean, proc: () -> Unit, interval: Seconds = 1.0, delta: Seconds = 2.0) {
    if (!cond()) {
        Sleep(interval)
        Retry(cond, proc, interval + delta, delta)
    } else {
        proc()
    }
}