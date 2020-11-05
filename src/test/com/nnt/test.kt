package com.nnt

import com.nnt.core.*
import com.nnt.server.EmptyTransaction
import com.nnt.server.Transaction
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@model()
open class A {

    init {
        proc()
    }

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

    open fun proc() {
        println("A")
    }
}

class B : A() {
    override fun proc() {
        println("B")
    }
}

class RTest : IRouter {
    override val action = "test"

    override fun config(node: JsonObject) {
        // pass
    }

    @action(A::class, [expose], "测试")
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
        Assertions.assertEquals(t["a"]!!.asInteger(), 1)
        Assertions.assertTrue(t["b"]!!.isNull)
        Assertions.assertEquals(toJson(t), jsonstr)

        var jsobj = JsonObject.UnMap(B())
        var str = jsobj.toString()
        Assertions.assertEquals(str, """{"a":0,"b":"abc","d":1,"e":1.1}""")
        var obja = JsonObject.Map<A>(jsobj)
        Assertions.assertTrue(obja.e == 1.1)

        str = toJson(mapOf("a" to 123, "b" to null))
        Assertions.assertEquals(str, """{"a":123,"b":null}""")

        str = toJson(listOf("a", 123, null))
        Assertions.assertEquals(str, """["a",123,null]""")

        val om = toJsonObject(mapOf("a" to 1))!!
        Assertions.assertEquals(om["a"]!!.asInteger(), 1)

        val ol = toJsonObject(listOf(0, 1, 2, 3, 4))!!
        Assertions.assertEquals(ol[1]!!.asInteger(), 1)
    }

    @Test
    fun TestProto() = runBlocking {
        // 测试模型填充
        val trans = EmptyTransaction()
        trans.action = "test.test"
        trans.params = mapOf("a" to 1, "b" to "cde")
        trans.parser = com.nnt.server.parser.Jsobj()
        trans.render = com.nnt.server.render.Json()

        val router = RTest()

        launch {
            trans.begin()
            trans.modelize(router)
            trans.collect()
        }.join()
    }
}