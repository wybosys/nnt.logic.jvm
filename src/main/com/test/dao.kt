package com.test

import com.nnt.store.AffectedRows

// dao入口，通过 resources/mybatis/sample.xml 绑定，并通过 app.json 进行注册
interface Sample {
    fun listEchoo(): List<Echoo>
    fun echoo(m: Echoo)
    fun clearEchoo(): AffectedRows
    fun updateEchoo(m: Echoo): AffectedRows
}

// dao使用的模型类
class Echoo {

    var id: Int = 0
    var input: String = ""
    var output: String = ""

    override fun toString(): String {
        return "${id} ${input} ${output}"
    }
}