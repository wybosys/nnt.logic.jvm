package com.nnt.logic.logger

import com.nnt.logic.config.Attribute
import com.nnt.logic.core.Jsonobj

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

    private fun isAllow(filter: String): Boolean {
        return _filters.contains(filter)
    }

    fun config(cfg: Jsonobj): Boolean {
        if (!cfg.has("filter"))
            return false
        _filters = Filter.Explode(cfg["filter"].asText())
        return true
    }

    abstract fun log(msg: String);
}