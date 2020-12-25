package com.nnt.core

enum class Level(val v: Int) {
    SPECIAL(9),
    CUSTOM(8),
    DEBUG(7),
    INFO(6),
    NOTICE(5),
    WARNING(4),
    ERROR(3),
    ALERT(2),
    CRITICAL(1),
    EMERGENCE(0),
    EMERGENCY(0)
}

fun GetStackElementMessage(err: StackTraceElement): String {
    return "${err.methodName}@${err.className}::${err.fileName}#${err.lineNumber})"
}

fun GetExceptionMessage(err: Throwable): String {
    val arr = mutableListOf<String>()

    // 基础报错信息
    val str = err.message
    if (str != null)
        arr.add(str)

    /*
    // 只输出前两行
    val sck = err.stackTrace
    if (sck.size > 0) {
        sb.append(GetStackElementMessage(sck[0])).append("; ")
    }
    if (sck.size > 1) {
        sb.append(GetStackElementMessage(sck[1])).append("; ")
    }
    */

    // 输出完整堆栈
    arr.add(err.stackTraceToString())

    return arr.joinToString("; ")
}

object logger {

    var log: (str: String) -> Unit = {
        println(it)
    }

    var warn: (str: String) -> Unit = {
        println(it)
    }

    var info: (str: String) -> Unit = {
        println(it)
    }

    var fatal: (str: String) -> Unit = {
        println(it)
    }

    var exception: (err: Throwable) -> Unit = {
        println(GetExceptionMessage(it))
    }

    var error: (str: String) -> Unit = {
        println(it)
    }

    var assert: (v: Boolean, str: String) -> Unit = { v: Boolean, str: String ->
        if (!v) {
            fatal(str)
        }
    }

    // 放在判定序列中最后一个命中提示
    fun orLog(str: String, def: Boolean = false): Boolean {
        log(str)
        return def
    }

    fun orWarn(str: String, def: Boolean = false): Boolean {
        warn(str)
        return def
    }

    fun orInfo(str: String, def: Boolean = false): Boolean {
        info(str)
        return def
    }

    fun orFatal(str: String, def: Boolean = false): Boolean {
        fatal(str)
        return def
    }

    fun orError(str: String, def: Boolean = false): Boolean {
        error(str)
        return def
    }

}