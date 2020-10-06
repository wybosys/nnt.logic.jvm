package com.nnt

import com.nnt.core.ByteUnit
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
}