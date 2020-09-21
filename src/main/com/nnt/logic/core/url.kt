package com.nnt.logic.core

import java.io.File

typealias FnSchemeResolver = (String) -> String

// 当前的运行目录
var ROOT = File("/").absolutePath
var HOME = File("").absolutePath

val schemes = mutableMapOf<String, FnSchemeResolver>()

///RegisterScheme("http") { it }
///RegisterScheme("https") { it }

// 注册处理器
fun RegisterScheme(scheme: String, proc: (body: String) -> String) {
    schemes[scheme] = proc
}

// 展开url
// 如果包含 :// 则拆分成 scheme 和 body，再根绝 scheme 注册的转换器转换
// 否则按照 / 来打断各个部分，再处理 ~、/ 的设置
fun expand(url: String): String? {
    if (url.indexOf("://") != -1) {
        val ps = url.split("://")
        val proc = schemes.get(ps[0])
        if (proc == null) {
            logger.fatal("没有注册该类型${ps[0]}的处理器")
            return null
        }
        return proc(ps[1]);
    }

    val ps = url.split("/").toMutableList()
    if (ps[0] == "~") {
        if (IS_JAR) {
            // 运行为jar中时直接删除
            ps.removeAt(0)
        } else {
            // 外部时设置为resource目录
            ps[0] = "${HOME}/src/main/resources"
        }
    } else if (ps[0] == "") {
        ps[0] = ROOT
    } else {
        return File(url).canonicalPath
    }
    return ps.joinToString(separator = "/")
}
