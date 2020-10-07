package com.test

interface Sample {
    fun listEchoo(): List<Echoo>
    fun echoo(m: Echoo)
}

class Echoo {

    var id: Int = 0
    var input: String = ""
    var output: String = ""

    override fun toString(): String {
        return "${id} ${input} ${output}"
    }
}