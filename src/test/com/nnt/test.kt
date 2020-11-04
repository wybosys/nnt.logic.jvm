package com.nnt

import com.nnt.core.ByteUnit
import com.nnt.core.toJson
import com.nnt.core.toJsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class A {
    var a = 0
    var b = "abc"
    var c = null
    var d = 1
    var e = 1.1
}

class Test {

    @Test
    fun Test0() {
        var a = ByteUnit()
        a.p = 1
        a.t = 2
        a.m = 3
        a.k = 4
        a.b = 1025
        var b = ByteUnit()
        b.fromstr(a.tostr()!!)
        b.u = 1000
        b.u = 1024
        Assertions.assertEquals(b, a)

        var c = ByteUnit()
        c.u = 1000
        c.fromstr("1.1M200")
        Assertions.assertEquals(c.m, 1)
        Assertions.assertEquals(c.k, 100)
        Assertions.assertEquals(c.b, 200)
        Assertions.assertEquals(c.bytes, 1100200)
    }

    @Test
    fun Test1() {
        val jsonstr = """{"a":1,"b":null}"""
        val t = toJsonObject(jsonstr)!!
        Assertions.assertTrue(t["a"].asInt() == 1)
        Assertions.assertTrue(t["b"].isNull)
        Assertions.assertEquals(toJson(t), jsonstr)

        var str = toJson(A())
        Assertions.assertEquals(str, """{"a":0,"b":"abc","d":1,"e":1.1}""")
        var obja = toJsonObject(str, A::class.java)
        Assertions.assertTrue(obja.e == 1.1)

        str = toJson(mapOf("a" to 123, "b" to null))
        Assertions.assertEquals(str, """{"a":123,"b":null}""")

        str = toJson(listOf("a", 123, null))
        Assertions.assertEquals(str, """["a",123,null]""")

        val om = toJsonObject(mapOf("a" to 1))!!
        Assertions.assertEquals(om["a"].asInt(), 1)
    }
}