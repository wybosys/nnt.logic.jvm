package com.nnt.core

import org.reflections.Reflections
import org.reflections.scanners.Scanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

// File("jar:file:/home/develop/github/nnt.logic.jvm/build/libs/logic-1.0-SNAPSHOT.jar!/BOOT-INF/classes!/app.json").exists()

class JarInfo {

    // jar包的路径
    var path: String = ""

    // jar包的home路径
    var home: String = ""
}

class JvmClass : Comparable<JvmClass> {

    // 类名称
    var name = ""

    // p父包
    var pkg: JvmPackage? = null

    // class指针
    lateinit var clazz: KClass<*>

    // 子类列表
    private var _children: MutableMap<KClass<*>, JvmClass>? = null

    // 计算依赖
    fun calcDepends(classes: Map<KClass<*>, JvmClass>) {
        clazz.allSuperclasses.forEach {
            val tgt = classes[it]
            if (tgt == null)
                return@forEach
            if (tgt._children == null)
                tgt._children = mutableMapOf()
            tgt._children!![clazz] = this
        }
    }

    override fun compareTo(other: JvmClass): Int {
        val l = _children?.size ?: 0
        val r = other._children?.size ?: 0
        return r - l
    }
}

class JvmPackage {

    // package名称
    private var _name = ""

    var name: String
        get() {
            return _name
        }
        set(value) {
            val iter = value.split(".").iterator()
            var cur = this
            cur._name = iter.next()
            while (iter.hasNext()) {
                val nm = iter.next()
                var curpkg = cur._packages[nm]
                if (curpkg == null) {
                    curpkg = JvmPackage()
                    curpkg._name = nm
                    curpkg.pkg = cur
                    cur._packages[nm] = curpkg
                }
                cur = curpkg
            }
        }

    // 仅包含第一级子类
    private val _classes = mutableMapOf<String, JvmClass>()
    val classes: Map<String, JvmClass> get() = _classes

    // 仅包含第一级子包
    private val _packages = mutableMapOf<String, JvmPackage>()
    val packages: Map<String, JvmPackage> get() = _packages

    // 父包
    var pkg: JvmPackage? = null

    // 展开包名
    fun expand(name: String): JvmPackage? {
        val iter = name.split(".").iterator()
        var nm = iter.next()
        if (nm != _name)
            return null
        if (!iter.hasNext())
            return null
        nm = iter.next()
        var cur = _packages[nm]
        if (cur == null) {
            cur = JvmPackage()
            cur._name = nm
            cur.pkg = this
            _packages[nm] = cur
        }
        while (iter.hasNext()) {
            nm = iter.next()
            var t = cur!!._packages[nm]
            if (t == null) {
                t = JvmPackage()
                t._name = nm
                t.pkg = cur
                cur._packages[nm] = t
            }
            cur = t
        }
        return cur
    }

    // 清空
    fun clear() {
        _name = ""
        _classes.clear()
        _packages.clear()
    }

    // 添加类
    fun add(clz: KClass<*>) {
        val pkg = expand(clz.java.packageName)
        if (pkg == null)
            return

        val t = JvmClass()
        if (clz.simpleName == null) {
            logger.warn("遇到一个name为null的class ${clz}")
            return
        }

        t.name = clz.simpleName!!
        t.clazz = clz
        t.pkg = pkg
        pkg._classes[t.name] = t
    }

    // 所有类
    fun allClassesList(): List<JvmClass> {
        val r = mutableListOf<JvmClass>()
        r.addAll(r.size, _classes.values.toList())
        _packages.forEach { _, sub ->
            r.addAll(r.size, sub.allClassesList())
        }
        return r
    }

    fun allClassesSet(): Set<JvmClass> {
        val r = mutableSetOf<JvmClass>()
        r.addAll(_classes.values)
        _packages.forEach { _, sub ->
            r.addAll(sub.allClassesList())
        }
        return r
    }

    fun allClassesMap(): Map<KClass<*>, JvmClass> {
        val r = mutableMapOf<KClass<*>, JvmClass>()
        _classes.forEach { _, clz ->
            r[clz.clazz] = clz
        }
        _packages.forEach { _, sub ->
            sub.allClassesList().forEach {
                r[it.clazz] = it
            }
        }
        return r
    }

    // 按照依赖度排序
    fun sorted(): List<JvmClass> {
        val clss = allClassesMap()
        // 计算依赖
        clss.forEach { _, clz ->
            clz.calcDepends(clss)
        }
        // 排序
        return clss.values.sorted()
    }

    // 过滤
    fun filter(proc: (cls: JvmClass) -> Boolean) {
        val t = _classes.filterValues { clz ->
            proc(clz)
        }
        _classes.clear()
        _classes.putAll(t)

        _packages.forEach { _, sub ->
            sub.filter(proc)
        }
    }

    // 查找类
    fun findClass(fullname: String): JvmClass? {
        val ps = fullname.split(".")
        if (ps.size > 1) {
            val pkg = findPackage(ps.subList(0, ps.size - 1))
            return pkg?._classes?.get(ps.last())
        }
        return classes[ps.first()]
    }

    // 查找包
    fun findPackage(fullname: String): JvmPackage? {
        return findPackage(fullname.split("."))
    }

    fun findPackage(names: List<String>): JvmPackage? {
        if (names[0] != _name)
            return null
        if (names.size == 1)
            return this
        val sub = _packages[names[1]]
        return sub?.findPackage(names.subList(1, names.size))
    }
}

// 用来筛选package中类的过滤器
class JvmPackageFilter {

    // 根据基类筛选
    var base: KClass<*> = Any::class

    // 根据包装筛选
    var annotation: KClass<*>? = null
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
        fun LoadPackage(path: String, filter: JvmPackageFilter = JvmPackageFilter()): JvmPackage? {
            val scanners = mutableListOf<Scanner>()
            scanners.add(SubTypesScanner(false))
            if (filter.annotation != null) {
                scanners.add(TypeAnnotationsScanner())
            }

            val reflections = Reflections(ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(path))
                .setScanners(*scanners.toTypedArray())
                .filterInputsBy(FilterBuilder().includePackage(path))
            )

            val classes: Set<Class<*>>
            val enums: Set<Class<*>>

            if (filter.annotation != null) {
                @Suppress("UNCHECKED_CAST")
                classes = reflections.getTypesAnnotatedWith(filter.annotation!!.java as Class<Annotation>)

                enums = setOf()
            } else {
                classes = reflections.getSubTypesOf(filter.base.java)
                enums = reflections.getSubTypesOf(Enum::class.java)
            }

            // 组装
            val r = JvmPackage()
            r.name = path
            classes.forEach {
                r.add(it.kotlin)
            }
            enums.forEach {
                r.add(it.kotlin)
            }
            return r
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

