package com.test.app.model

import com.nnt.core.*

@model([enumm])
enum class EchoType(val value: Int) {
    TEST(88)
}

@model()
class Echoo {

    @string(1, [com.nnt.core.input], "输入")
    var input: String = ""

    @string(2, [com.nnt.core.output], "输出")
    var output: String = ""

    @integer(3, [com.nnt.core.output], "服务器时间")
    var time: Long = 0

    @json(4, [com.nnt.core.output])
    var json: Jsonobj? = null

    @map(5, String::class, Int::class, [com.nnt.core.output])
    var map = mapOf<String, Int>()

    @array(6, Double::class, [com.nnt.core.output])
    var array = arrayOf<Double>()

    @enumerate(7, EchoType::class, [com.nnt.core.output])
    var enm = EchoType.TEST

    @type(8, Null::class, [com.nnt.core.output])
    var nullval: Null? = null
}