package com.nnt.manager

import com.nnt.core.Seconds
import com.nnt.core.URI

class Config {

    companion object {

        // DEBUG模式
        var DEBUG = false

        // DEVELOP模式，和debug的区别，develop用来部署开发服务器，debug用来做本地开发，会影响到app.json中对服务器的启动处理
        var DEVELOP = false

        // PUBLISH模式，和release类似，除了会使用线上配置外，其他又和develop一致
        var PUBLISH = false

        // 正式版模式
        var DISTRIBUTION = true

        // 本地模式
        var LOCAL = false

        // 容器部署
        var DEVOPS = false

        // 内网测试容器部署
        var DEVOPS_DEVELOP = false

        // 外网容器部署
        var DEVOPS_RELEASE = true

        // sid过期时间，此框架中时间最小单位为秒
        var SID_EXPIRE = 86400

        // clientid 过期时间
        var CID_EXPIRE = 600

        // model含有最大fields的个数
        var MODEL_FIELDS_MAX = 100

        // transaction超时时间
        var TRANSACTION_TIMEOUT: Seconds = 20f

        // 是否允许客户端访问
        var CLIENT_ALLOW = false

        // 是否允许服务端访问
        var SERVER_ALLOW = true

        // 白名单
        var ACCESS_ALLOW = mutableListOf<String>()

        // 黑名单
        var ACCESS_DENY = mutableListOf<String>()

        // 服务端缓存目录
        var CACHE = URI("cache")

        // 最大下载文件的大小
        var FILESIZE_LIMIT = 10485760; // 10M

        // 时区
        var TIMEZONE = "Asia/Shanghai"

    }
}

// 判断是否是开发版
fun IsDebug(): Boolean {
    return Config.DEBUG || Config.DEVELOP || Config.PUBLISH
}

// 是否是正式版
fun IsRelease(): Boolean {
    return Config.DISTRIBUTION
}

fun <T> DebugValue(d: T, r: T): T {
    return if (Config.DISTRIBUTION) r else d
}

// 支持DEVOPS的架构判断
fun IsDevops(): Boolean {
    return System.getenv("DEVOPS") != null
}

fun IsDevopsDevelop(): Boolean {
    return System.getenv("DEVOPS") != null && System.getenv("DEVOPS_RELEASE") != null
}

fun IsDevopsRelease(): Boolean {
    return System.getenv("DEVOPS_RELEASE") != null
}

fun IsLocal(): Boolean {
    return System.getenv("DEVOPS") == null
}