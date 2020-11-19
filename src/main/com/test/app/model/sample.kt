package com.test.app.model

import com.nnt.core.*
import com.nnt.store.colstring
import com.nnt.store.table

@model([enumm])
enum class EchoType(val value: Int) {
    TEST(88)
}

@model()
@table("echoo")
class Echoo {

    @string(1, [com.nnt.core.input], "输入")
    @colstring("input")
    var input: String = ""

    @string(2, [com.nnt.core.output], "输出")
    @colstring("output")
    var output: String = ""

    @integer(3, [com.nnt.core.output], "服务器时间")
    var time: Long = 0

    @json(4, [com.nnt.core.output])
    var json: JsonObject? = null

    @map(5, String::class, Int::class, [com.nnt.core.output])
    var map = mapOf<String, Int>()

    @array(6, Double::class, [com.nnt.core.output])
    var array = listOf<Double>()

    @enumerate(7, EchoType::class, [com.nnt.core.output])
    var enm = EchoType.TEST

    @type(8, Null::class, [com.nnt.core.output])
    var nullval: Null? = null

    @boolean(9, [com.nnt.core.input])
    var ok: Boolean = false

    @integer(10, [com.nnt.core.input])
    var value: Int = 0

    @string(11, [com.nnt.core.input, com.nnt.core.output])
    var content: String = ""

    @money(12, [com.nnt.core.input, com.nnt.core.output])
    var money: IntFloat? = null
}