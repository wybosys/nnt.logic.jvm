package com.nnt.logic.logger

import com.nnt.logic.core.STATUS
import com.nnt.logic.manager.Config

class Console : AbstractLogger() {

    override fun log(msg: String, status: STATUS?) {
        if (Config.DEVELOP || Config.DEBUG)
            println(msg)
    }

    override fun warn(msg: String, status: STATUS?) {
        println(msg)
    }

    override fun info(msg: String, status: STATUS?) {
        println(msg)
    }

    override fun fatal(msg: String, status: STATUS?) {
        println(msg)
    }

    override fun exception(msg: String, status: STATUS?) {
        println(msg)
    }
}
