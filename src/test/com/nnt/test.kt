package com.nnt

import com.nnt.core.*
import com.nnt.server.EmptyTransaction
import com.nnt.server.Transaction
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@model()
class A {

    @integer(1, [input, output])
    var a = 0

    @string(2, [input, output])
    var b = "abc"

    @type(3, Null::class, [input, output, optional])
    var c = null

    @integer(4, [input, optional])
    var d = 1

    @double(5, [input, output, optional])
    var e = 1.1
}

class RTest : IRouter {
    override val action = "test"

    override fun config(node: Jsonobj) {
        // pass
    }

    @action(A::class)
    suspend fun test(trans: Transaction) {
        trans.submit()
    }
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

    @Test
    fun TestProto() = runBlocking {
        // 测试模型填充
        val trans = EmptyTransaction()
        trans.action = "test.test"
        trans.params = mapOf("a" to 1, "b" to "cde")

        val router = RTest()

        launch {
            trans.modelize(router)
            trans.collect()
        }.join()
    }
}