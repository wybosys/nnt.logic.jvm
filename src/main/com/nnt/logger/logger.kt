package com.nnt.logger

import com.nnt.config.Attribute
import com.nnt.core.Jsonobj
import com.nnt.core.STATUS

class Filter {

    companion object {
        val LOG = "log"
        val WARN = "warn"
        val INFO = "info"
        val FATAL = "fatal"
        val EXCEPTION = "exception"
        val ALL = "all"

        fun Explode(cfg: String): Set<String> {
            val r = mutableSetOf<String>()
            val vs: List<String>
            if (cfg == ALL) {
                vs = listOf(
                    LOG,
                    WARN,
                    INFO,
                    FATAL,
                    EXCEPTION
                )
            } else {
                vs = Attribute.FromString(cfg)
            }
            vs.forEach() {
                r.add(it)
            }
            return r
        }
    }
}

abstract class AbstractLogger {

    private lateinit var _filters: Set<String>

    fun isAllow(filter: String): Boolean {
        return _filters.contains(filter)
    }

    fun config(cfg: Jsonobj): Boolean {
        if (!cfg.has("filter"))
            return false
        _filters = Filter.Explode(cfg["filter"].asText())
        return true
    }

    abstract fun log(msg: String, status: STATUS? = null)
    abstract fun warn(msg: String, status: STATUS? = null)
    abstract fun info(msg: String, status: STATUS? = null)
    abstract fun fatal(msg: String, status: STATUS? = null)
    abstract fun exception(msg: String, status: STATUS? = null)
}