package com.nnt.test

interface ITest {

    fun Test()
}

class TestImpl : ITest {

    override fun Test() {
        println("HAHA")
    }
}