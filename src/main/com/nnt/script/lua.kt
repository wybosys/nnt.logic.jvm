package com.nnt.script

import com.nnt.core.logger
import com.nnt.manager.Config
import org.luaj.vm2.Globals
import org.luaj.vm2.lib.jse.JsePlatform

class Lua {

    private val _G: Globals

    private val _package_paths = mutableListOf<String>()

    init {
        if (Config.PUBLISH) {
            _G = JsePlatform.standardGlobals()
        } else {
            _G = JsePlatform.debugGlobals()
        }

        // 同步一些默认的lua设置
        val pkg_path = get("package.path") as String
        pkg_path.split(":").forEach {
            _package_paths.add(it)
        }
    }

    /**
     * 添加查找package的目录
     */
    fun addPackgePath(dir: String) {
        _package_paths.add("${dir}/?.lua")
        set("package.path", _package_paths.joinToString(";"))
    }

    /**
     * 执行lua语句
     * @param source 代码内容
     */
    fun eval(source: String): Any? {
        try {
            val ck = _G.load(source)
            val r = ck.call()
            return FromLv(r)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    /** 加载文件
     * @param path 相对于资源包的路径
     */
    fun loadfile(path: String): Boolean {
        try {
            val ck = _G.loadfile(path)
            ck.call()
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    /**
     * 获得路径对应的数据
     */
    fun get(keypath: String): Any? {
        return getv(*keypath.split(".").toTypedArray())
    }

    /**
     * 获得路径对应的数据
     */
    fun getv(vararg kps: String): Any? {
        return ObjectGet(_G, *kps)
    }

    /**
     * 按照路径设置数据
     */
    fun set(keypath: String, value: Any?): Boolean {
        return setv(value, *keypath.split(".").toTypedArray())
    }

    /**
     * 按照路径设置数据
     */
    fun setv(value: Any?, vararg kps: String): Boolean {
        return ObjectSet(_G, value, *kps)
    }

}

