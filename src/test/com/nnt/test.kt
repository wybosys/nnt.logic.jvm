package com.nnt

import com.nnt.core.*
import com.nnt.server.EmptyTransaction
import com.nnt.server.Routers
import com.nnt.server.Transaction
import com.nnt.thirds.dust.DustCompiler
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

    @decimal(5, [input, output, optional])
    var e = 1.1

    open fun proc() {
        println("A")
    }
}

@model([], A::class)
class B : A() {

    @integer(1, [input, output])
    var ba = 0

    override fun proc() {
        println("B")
    }

    companion object {

        val sstr = "STATIC B STRING"
        val this_ = 123
    }
}

class RTest : AbstractRouter() {

    override val action = "test"

    @action(A::class, [expose], "测试")
    suspend fun test(trans: Transaction) {
        trans.submit()
    }
}

enum class TestType(val value: Int) {
    XXXX(1),
    YYYY(2)
}

class Test {

    @Test
    fun TestJvm() {
        val filter = JvmPackageFilter()
        filter.base = Any::class
        // filter.base = AbstractRouter::class
        // filter.annotation = model::class
        val pkg = Jvm.LoadPackage("com.test.app.model", filter)!!
        pkg.filter {
            FindModel(it.clazz) != null
        }
        Assertions.assertTrue(pkg.findClass("com.test.app.model.a.A") != null)
        Assertions.assertTrue(pkg.findClass("com.test.app.model.a.D") == null)

        val classes = pkg.sorted()
        println(classes)
    }

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

        Assertions.assertEquals(EnumValue(TestType.XXXX), 1)
        Assertions.assertEquals(ToEnum(TestType::class, 1), TestType.XXXX)
        Assertions.assertEquals(ToEnum(TestType::class, "XXXX"), TestType.XXXX)

        var d = flat(B::class)
        Assertions.assertEquals(d["this"], 123)

        // var id = IntFloat(0, 10)
        // var id2 = id + 1
        // id = id + 2
        // Assertions.assertEquals(id2.origin, 10)
        // Assertions.assertEquals(id.origin, 20)
    }

    @Test
    fun Test1() {
        val jsonstr = """{"a":1,"b":null}"""
        val t = toJsonObject(jsonstr)!!
        Assertions.assertEquals(t["a"]!!.asInteger(), 1)
        Assertions.assertTrue(t["b"]!!.isNull)
        Assertions.assertEquals(toJson(t), jsonstr)

        var jsobj = JsonObject.UnMap(A())
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

        val b = B()
        val fb = flat(b)
        Assertions.assertTrue(fb is Map<*, *>)
    }

    @Test
    fun TestProto() = runBlocking {
        // 测试模型填充
        val trans = EmptyTransaction()
        trans.action = "test.test"
        trans.params = mapOf("a" to 1, "b" to "cde")
        trans.parser = com.nnt.server.parser.Jsobj()
        trans.render = com.nnt.server.render.Json()

        val routers = Routers()
        routers.register(RTest())

        launch {
            routers.process(trans)
        }.join()
    }

    @Test
    fun TestTemplate() = runBlocking {
        val tpl = DustCompiler()
        Assertions.assertEquals(tpl.compile("{.test}", "test"), true)
        Assertions.assertEquals(tpl.render("test", mapOf<String, Any>("test" to 123)), "123")
    }
}