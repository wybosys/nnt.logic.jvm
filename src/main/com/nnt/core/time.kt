package com.nnt.core

import kotlinx.coroutines.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.Month

class DateTimeRange(
    var from: UnixTimestamp = 0, // 开始
    var to: UnixTimestamp = 0, // 结束，比如小时 [0, 60)
) {}

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

// UNIX时间戳精确到秒
typealias UnixTimestamp = Long

// 精确到毫秒的时间戳
typealias FullTimestamp = Long

class DateTime {

    constructor() : this(Current()) {
        // pass
    }

    constructor(ts: UnixTimestamp) {
        timestamp = ts
    }

    constructor(dt: LocalDateTime) {
        _date = dt
        _timestamp = java.sql.Timestamp.valueOf(_date).time / 1000
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

    private var _changed = false
    private var _date = InstanceDate()
    private var _timestamp: UnixTimestamp = 0

    var timestamp: UnixTimestamp
        get() {
            if (_changed) {
                _timestamp = java.sql.Timestamp.valueOf(_date).time / 1000
                _changed = false
            }
            return _timestamp
        }
        set(value) {
            if (_timestamp == value)
                return
            _timestamp = value
            _date = java.sql.Timestamp(value * 1000).toLocalDateTime()
        }

    var year: Int
        get() {
            return _date.year
        }
        set(value) {
            _changed = true
            _date = _date.withYear(value)
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
            return _date.month.value - 1
        }
        set(value) {
            _changed = true
            _date = _date.withMonth(value + 1)
        }

    var hmonth: Month
        get() {
            return _date.month
        }
        set(value) {
            _changed = true
            _date = _date.withMonth(value.value)
        }

    var day: Int
        get() {
            return _date.dayOfMonth - 1
        }
        set(value) {
            _changed = true
            _date = _date.withDayOfMonth(value + 1)
        }

    var hday: Int
        get() {
            return _date.dayOfMonth
        }
        set(value) {
            _changed = true
            _date = _date.withDayOfMonth(value)
        }

    var hour: Int
        get() {
            return _date.hour
        }
        set(value) {
            _changed = true
            _date = _date.withHour(value)
        }

    var minute: Int
        get() {
            return _date.minute
        }
        set(value) {
            _changed = true
            _date = _date.withMinute(value)
        }

    var second: Int
        get() {
            return _date.second
        }
        set(value) {
            _changed = true
            _date = _date.withSecond(value)
        }

    val weekday: Int
        get() {
            return _date.dayOfWeek.value - 1
        }

    val hweekday: DayOfWeek
        get() {
            return _date.dayOfWeek
        }

    /** 计算diff-year，根绝suffix的类型返回对应的类型 */
    fun <T : Number> dyears(up: Boolean = true, suffix: T? = null): Long {
        val v = Dyears(_timestamp, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dyears(up: Boolean = true, suffix: String): String {
        val v = Dyears(_timestamp, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dmonths(up: Boolean = true, suffix: T? = null): Long {
        val v = Dmonths(_timestamp, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dmonths(up: Boolean = true, suffix: String): String {
        val v = Dmonths(_timestamp, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> ddays(up: Boolean = true, suffix: T? = null): Long {
        val v = Ddays(_timestamp, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun ddays(up: Boolean = true, suffix: String): String {
        val v = Ddays(_timestamp, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dhours(up: Boolean = true, suffix: T? = null): Long {
        val v = Dhours(_timestamp, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dhours(up: Boolean = true, suffix: String): String {
        val v = Dhours(_timestamp, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dminutes(up: Boolean = true, suffix: T? = null): Long {
        val v = Dminutes(_timestamp, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dminutes(up: Boolean = true, suffix: String): String {
        val v = Dminutes(_timestamp, up)
        return if (v > 0) "$v$suffix" else ""
    }

    fun <T : Number> dseconds(up: Boolean = true, suffix: T? = null): Long {
        val v = Dseconds(_timestamp, up)
        return if (suffix != null) v + suffix.toLong() else v
    }

    fun dseconds(up: Boolean = true, suffix: String): String {
        val v = Dseconds(_timestamp, up)
        return if (v > 0) "$v$suffix" else ""
    }

    // 当前分钟的起始
    fun minuteRange(): DateTimeRange {
        val from = this.timestamp - this.second
        return DateTimeRange(
            from,
            from + 60 - 1 // 整数算在下一刻
        )
    }

    // 当前小时的起始
    fun hourRange(): DateTimeRange {
        val from = this.timestamp - this.minute * DateTime.MINUTE - this.second
        return DateTimeRange(
            from,
            from + DateTime.HOUR - 1
        )
    }

    // 一天的起始
    fun dayRange(): DateTimeRange {
        val from = this.timestamp - this.hour * DateTime.HOUR - this.minute * DateTime.MINUTE - this.second
        return DateTimeRange(
            from,
            from + DateTime.DAY - 1
        )
    }

    // 本周的起始
    fun weekRange(): DateTimeRange {
        val from =
            this.timestamp - this.weekday * DateTime.DAY - this.hour * DateTime.HOUR - this.minute * DateTime.MINUTE - this.second
        return DateTimeRange(
            from,
            from + DateTime.WEEK - 1
        )
    }

    // 本月的起始
    fun monthRange(): DateTimeRange {
        val cur = LocalDateTime.of(_date.year, _date.month, 0, 0, 0, 0)
        val next = cur.plusMonths(1)
        return DateTimeRange(
            java.sql.Timestamp.valueOf(cur).time / 1000,
            java.sql.Timestamp.valueOf(next).time / 1000 - 1
        )
    }

    override fun toString(): String {
        return _date.toString()
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


        fun Dyears(ts: UnixTimestamp, @SuppressWarnings("UnusedParameters") up: Boolean = true): Long {
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