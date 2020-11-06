package com.test.app.router

import com.nnt.component.TODAY_RANGE
import com.nnt.core.*
import com.test.app.model.Echoo
import com.test.app.model.Trans

class RSample : IRouter {

    override val action = "test"

    override fun config(node: JsonObject): Boolean {
        return true
    }

    @action(Echoo::class)
    suspend fun echo(trans: Trans) {
        val m = trans.model as Echoo
        m.output = m.input
        m.time = DateTime.Current()
        m.json = toJsonObject(mapOf("today" to TODAY_RANGE))
        m.map = mapOf("a0" to 0, "b1" to 1)
        m.array = listOf(0.0, 1.0, 2.0, 3.0)
        trans.submit()
    }

}