package com.nnt.config

import com.ctrip.framework.apollo.Config
import com.ctrip.framework.apollo.ConfigChangeListener
import com.ctrip.framework.apollo.ConfigService
import com.ctrip.framework.apollo.model.ConfigChangeEvent
import com.nnt.core.JsonObject
import com.nnt.core.toJsonObject
import com.nnt.signals.Object
import com.nnt.signals.kSignalChanged

object Apollo : Object() {

    init {
        signals.register(kSignalChanged)
    }

    private var _enabled: Boolean = false

    // 是否启用
    val enabled get() = _enabled

    private val _svc: Config by lazy {
        val r = ConfigService.getAppConfig()
        r.addChangeListener(object : ConfigChangeListener {
            override fun onChange(changeEvent: ConfigChangeEvent) {
                if (changeEvent.isChanged(key)) {
                    val txt = _svc.getProperty(key, "")
                    _val = toJsonObject(txt)
                    signals.emit(kSignalChanged)
                }
            }
        })
        r
    }

    private var key: String = ""
    private var _val: JsonObject? = null

    fun config(cfg: JsonObject): Boolean {
        if (!cfg.has("host"))
            return false
        System.setProperty("apollo.configService", cfg["host"]!!.asString())
        if (!cfg.has("appid"))
            return false
        System.setProperty("app.id", cfg["appid"]!!.asString())
        if (!cfg.has("key"))
            return false
        key = cfg["key"]!!.asString()
        val txt = _svc.getProperty(key, "")
        _val = toJsonObject(txt)
        _enabled = true
        return true
    }

    fun value(): JsonObject? {
        return _val
    }
}