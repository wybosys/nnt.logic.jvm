package com.nnt.component

import com.nnt.core.DateTime
import com.nnt.core.DateTimeRange
import com.nnt.manager.CronTask
import com.nnt.manager.PerDay
import com.nnt.manager.PerHour
import java.time.Month

private class _TaskToday : CronTask() {

    override fun main() {
        val now = DateTime()
        TODAY_RANGE = now.dayRange()
        WEEKEND_RANGE = now.weekRange()
        TODAY_YEAR = now.hyear
        TODAY_MONTH = now.hmonth
        TODAY_DAY = now.hday
    }
}

var TODAY_RANGE = DateTimeRange()
var WEEKEND_RANGE = DateTimeRange()
var TODAY_DAY: Int = 0
var TODAY_MONTH: Month = Month.APRIL
var TODAY_YEAR: Int = 0

private class _TaskHour : CronTask() {

    override fun main() {
        val now = DateTime()
        CURRENT_HOUR_RANGE = now.hourRange()
    }
}

var CURRENT_HOUR_RANGE = DateTimeRange()

private class _TaskTest : CronTask() {
    override fun main() {
        println("TaskTest")
    }
}

fun TodayInit() {
    val tt = _TaskToday()
    tt.time = PerDay(1)
    tt.main()
    tt.start()

    val th = _TaskHour()
    th.time = PerHour(1)
    th.main()
    th.start()

    /*
    val test = _TaskTest()
    test.time = PerMinute(1)
    test.main()
    test.start()
     */
}
