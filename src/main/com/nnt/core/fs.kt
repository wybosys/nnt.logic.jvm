package com.nnt.core

import java.io.*
import java.io.File

class File(uri: URI) {

    val uri: URI = uri

    fun exists(): Boolean {
        if (uri.bundle) {
            return javaClass.getResource(uri.path) == null
        }

        return File(uri.path).exists()
    }

    fun readText(): String {
        if (uri.bundle) {
            val stm = javaClass.classLoader.getResourceAsStream(uri.path)
            val reader = BufferedReader(InputStreamReader(stm!!))

            // jar中运行时，没一行不带行尾，所以使用保护方式执行合并
            val lines = reader.readLines()
            var hasln = false
            if (lines.size > 0) {
                hasln = lines[0].endsWith("\n")
            }
            return lines.joinToString(if (hasln) "" else "\n")
        }

        return File(uri.path).readText()
    }

    fun open(): InputStream? {
        if (uri.bundle)
            return javaClass.classLoader.getResourceAsStream(uri.path)
        return FileInputStream(uri.path)
    }

    fun mkdirs(): Boolean {
        if (uri.bundle) {
            return false
        }

        return File(uri.path).mkdirs()
    }
}

class Stats {

}