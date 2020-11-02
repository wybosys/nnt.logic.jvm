package com.nnt.server

import com.nnt.core.Jsonobj

open class Rest : AbstractServer() {

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false
        return true
    }

    override fun start() {

    }

    override fun stop() {

    }

}