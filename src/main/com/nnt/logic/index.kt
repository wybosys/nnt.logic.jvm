package com.nnt.logic

import org.springframework.context.support.ClassPathXmlApplicationContext

class Index {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val ctx = ClassPathXmlApplicationContext("app.xml")
            ctx.start()
            System.`in`.read()
        }
    }
}