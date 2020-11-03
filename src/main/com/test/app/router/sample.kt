package com.test.app.router

import com.nnt.core.DateTime
import com.nnt.core.IRouter
import com.nnt.core.Jsonobj
import com.nnt.core.action
import com.test.app.model.Echoo
import com.test.app.model.Trans

class RSample : IRouter {

    override val action = "test"

    override fun config(node: Jsonobj) {
        // pass
    }

    @action(Echoo::class)
    fun echo(trans: Trans) {
        val m = trans.model as Echoo
        m.output = m.input
        m.time = DateTime.Current()
        // m.json = toJsonObject(mapOf("today" to TODAY_Range))
    }

}