package com.nnt.core

import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

// File("jar:file:/home/develop/github/nnt.logic.jvm/build/libs/logic-1.0-SNAPSHOT.jar!/BOOT-INF/classes!/app.json").exists()

class JarInfo {

    // jar包的路径
    var path: String = ""

    // jar包的home路径
    var home: String = ""
}

class JvmClass {

    // 类名称
    var name = ""

    // p父包
    var pkg: JvmPackage? = null
}

class JvmPackage {

    // package名称
    var name = ""

    // 包中的类
    val classes = mutableMapOf<String, JvmClass>()

    // 子包
    val packages = mutableMapOf<String, JvmPackage>()

    // 父包
    var pkg: JvmPackage? = null

    // 查找类
    fun findClass(fullname: String): JvmClass? {
        val ps = fullname.split(".")
        if (ps.size > 1) {
            val pkg = findPackage(ps.subList(0, ps.size - 1))
            return pkg?.findClass(ps.last())
        }
        return classes[ps.first()]
    }

    // 查找包
    fun findPackage(fullname: String): JvmPackage? {
        return findPackage(fullname.split("."))
    }

    fun findPackage(names: List<String>): JvmPackage? {
        if (names[0] != name)
            return null
        if (names.size == 1)
            return this
        return findPackage(names.subList(1, names.size))
    }
}

class Jvm {

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

        // 读取包
        fun LoadPackage(path: String): JvmPackage? {
            val reflections = Reflections(ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(path))
                .setScanners(SubTypesScanner())
            )
            val t = reflections.getSubTypesOf(Any::class.java)
            return null
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

