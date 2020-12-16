package com.nnt.script

import com.nnt.core.logger
import com.nnt.manager.Config
import org.luaj.vm2.Globals
import org.luaj.vm2.lib.jse.JsePlatform

class Lua {

    private val _G: Globals

    init {
        if (Config.PUBLISH) {
            _G = JsePlatform.standardGlobals()
        } else {
            _G = JsePlatform.debugGlobals()
        }

        // 同步一些默认的lua设置
    }

    /**
     * 执行lua语句
     * @param source 代码内容
     */
    fun eval(source: String): Any? {
        val ck = _G.load(source)
        try {
            ck.call()
            return null
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    /** 加载文件
     * @param path 相对于资源包的路径
     */
    fun loadfile(path: String): Boolean {
        val ck = _G.loadfile(path)
        try {
            ck.call()
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }
}