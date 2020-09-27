package com.nnt.logic.core

// File("jar:file:/home/develop/github/nnt.logic.jvm/build/libs/logic-1.0-SNAPSHOT.jar!/BOOT-INF/classes!/app.json").exists()

class JarInfo {

    // jar包的路径
    var path: String = ""

    // jar包的home路径
    var home: String = ""
}

private class Jvm {

    companion object {

        fun IsJar(): Boolean {
            val fp = Jvm::class.java.getResource("Jvm.class").toURI()
            return fp.scheme == "jar"
        }

        // 获取jar包信息
        fun GetJarInfo(): JarInfo {
            val fp = Jvm::class.java.getResource("Jvm.class").toURI()
            if (fp.scheme != "jar")
                return JarInfo()
            val fps = fp.toString().split("!")
            val r = JarInfo()
            r.path = fps[0].substring(9)
            r.home = fps[1]
            return r
        }

        // 获得jar包目录
        fun JarHome(): String {
            val fp = Jvm::class.java.getResource("Jvm.class").toURI()
            val fps = fp.toString().split("!")
            return "${fps[0]}!${fps[1]}!"
        }
    }
}

private object _IsJar {
    var VALUE = Jvm.IsJar()
}

private object _JarHome {
    var VALUE = Jvm.JarHome()
}

fun IsJar(): Boolean {
    return _IsJar.VALUE
}

fun JarHome(): String {
    return _JarHome.VALUE
}

