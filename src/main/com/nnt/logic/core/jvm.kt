package com.nnt.logic.core

private class Jvm {

    companion object {

        fun IsJar(): Boolean {
            val fp = Jvm::class.java.getResource("Jvm.class").toURI()
            return fp.scheme == "jar"
        }
    }
}

fun IsJar(): Boolean {
    return Jvm.IsJar()
}

// 是否运行为jar
val IS_JAR = IsJar()
