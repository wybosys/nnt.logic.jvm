package com.nnt.test

interface ITestService {

    fun Test()
}

class TestServiceImpl : ITestService {

    override fun Test() {
        println("HAHA")
    }
}