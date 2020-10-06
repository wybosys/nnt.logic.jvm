package com.nnt.core

import java.io.File

typealias FnSchemeResolver = (URI) -> Unit

// 当前的运行目录
var ROOT = File("/").absolutePath
var HOME = System.getProperty("user.dir")// File("").absolutePath

private val schemes = mutableMapOf<String, FnSchemeResolver>()

// 注册处理器
fun RegisterScheme(scheme: String, proc: (uri: URI) -> Unit) {
    schemes[scheme] = proc
}

class URI(uri: String) {

    var scheme: String = ""
    var path: String = ""
    var bundle: Boolean = false

    init {
        if (uri.indexOf("://") != -1) {
            // 包含scheme段
            val ps = uri.split("://")
            scheme = ps[0]
            path = ps[1]

            val proc = schemes.get(scheme)
            if (proc == null) {
                logger.fatal("没有注册该类型${ps[0]}的处理器")
            } else {
                proc(this)
            }
        } else {
            path = uri
        }

        // 展开url 
        // 如果包含 :// 则拆分成 scheme 和 body，再根绝 scheme 注册的转换器转换 
        // 否则按照 / 来打断各个部分，再处理 ~、/ 的设置
        val ps = path.split("/").toMutableList()
        if (ps[0] == "~") {
            // 外部时设置为resource目录
            ps[0] = HOME
        } else if (ps[0] == "") {
            ps[0] = ROOT
        } else {
            path = "${HOME}/${path}"
        }
        path = ps.joinToString(separator = "/")
    }

    override fun toString(): String {
        return path
    }
}
