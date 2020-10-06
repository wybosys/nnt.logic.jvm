package com.nnt.core

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

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
            val reader = BufferedReader(InputStreamReader(stm))
            return reader.readLines().joinToString("")
        }

        return File(uri.path).readText()
    }

    fun mkdirs(): Boolean {
        if (uri.bundle) {
            return false
        }

        return File(uri.path).mkdirs()
    }
}