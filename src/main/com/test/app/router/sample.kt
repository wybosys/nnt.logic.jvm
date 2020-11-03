package com.test.app.router

import com.nnt.core.IRouter
import com.nnt.core.Jsonobj
import com.nnt.core.action
import com.test.app.model.Echoo
import com.test.app.model.Trans

class RSample : IRouter {

    override val action = "test"

    @action(Echoo::class)
    fun echo(trans: Trans) {

    }

    override fun config(node: Jsonobj) {
        // pass
    }

}