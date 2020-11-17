package com.nnt.core

class DevopsConfig {
    var path = ""
    var client = false
    var domain = ""
}

object Devops {

    private val _cfg = DevopsConfig()

    init {
        val file = File(URI("bundle://devops.json"))
        if (file.exists()) {
            val cnt = file.readText()
            val cfg = toJsonObject(cnt)!!
            _cfg.path = cfg["path"]!!.asString()
            _cfg.client = cfg["client"]?.asBoolean() ?: false
            _cfg.domain = _cfg.path.substring(16)
        }
    }

    fun GetPath(): String {
        return _cfg.path
    }

    fun GetDomain(): String {
        return _cfg.domain
    }
}