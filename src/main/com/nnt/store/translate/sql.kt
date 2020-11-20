package com.nnt.store.translate

import com.nnt.core.JsonObject
import com.nnt.store.Filter
import com.nnt.store.OPERATORS
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Criteria {

    val sql: String get() = _sql
    val param: Array<*> get() = _param.toTypedArray()

    private var _empty = true
    private var _sql: String = ""
    private var _param = mutableListOf<Any?>()

    fun andWhere(sql: String, param: Any?) {
        if (_empty) {
            _sql = " where ${sql}"
            _empty = false
        } else {
            _sql += " and ${sql}"
        }

        _param.add(param)
    }

    fun orWhere(sql: String, param: Any?) {
        if (_empty) {
            _sql = " where ${sql}"
            _empty = false
        } else {
            _sql += " or ${sql}"
        }

        _param.add(param)
    }
}

class State {

    var and: Boolean = false
    var or: Boolean = false
    var key: String? = null

}

// 和Filter中定义的OPERATORS保持一一对应
val MYSQL_OPERATORS = listOf(">", ">=", "=", "!=", "<", "<=", "like");

fun ConvertOperator(cmp: String): String {
    return MYSQL_OPERATORS[OPERATORS.indexOf(cmp)]
}

val PATTERN_TIMESTAMP = Regex("""\d{4}-\d{1,2}-\d{1,2} \d{1,2}:\d{1,2}:\d{1,2}""");
val FMT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

fun ConvertValue(cmp: String, v: JsonObject): Any? {
    if (cmp == "search") {
        return "%${v.flat()}%"
    }
    var r = v.flat()
    if (r is String) {
        if (PATTERN_TIMESTAMP.matches(r)) {
            r = java.sql.Timestamp.valueOf(LocalDateTime.parse(r, FMT_TIMESTAMP))
        }
    }
    return r
}

fun Translate(f: Filter?, state: State? = null): Criteria {
    val q = Criteria()
    Translate(f, q, state)
    return q
}

fun Translate(f: Filter?, q: Criteria, state: State? = null) {
    if (f == null)
        return

    if (f.key == null) {
        if (f.ands.size > 0) {
            val st = State()
            st.and = true
            f.ands.forEach {
                Translate(it, q, st)
            }
        }

        if (f.ors.size > 0) {
            val st = State()
            st.or = true
            f.ors.forEach {
                Translate(it, q, st)
            }
        }

        if (f.operator != null && f.value != null) {
            val oper = ConvertOperator(f.operator!!)
            val v = ConvertValue(f.operator!!, f.value!!)
            if (state!!.and) {
                q.andWhere("${state.key} ${oper} ?", v)
            } else {
                q.orWhere("${state.key} ${oper} ?", v)
            }
        }
    }

    if (f.key != null) {
        val st = State()
        st.key = f.key

        if (f.ands.size > 0) {
            st.and = true
            f.ands.forEach {
                Translate(it, q, st)
            }
        }
    }
}