package com.test.app.router

import com.nnt.component.TODAY_RANGE
import com.nnt.core.AbstractRouter
import com.nnt.core.DateTime
import com.nnt.core.action
import com.nnt.core.toJsonObject
import com.nnt.store.GetTableInfo
import com.nnt.store.JdbcSession
import com.test.app.model.Echoo
import com.test.app.model.Trans

class RSample : AbstractRouter() {

    override val action = "test"
    
    @action(Echoo::class)
    suspend fun echo(trans: Trans) {
        val m = trans.model as Echoo

        val db = trans.db("mysql") as JdbcSession
        val ti = GetTableInfo(Echoo::class)!!
        db.update(
            "insert into ${ti.name} (input, output) values (?, ?)",
            m.input, m.output
        )

        m.output = m.input
        m.time = DateTime.Current()
        m.json = toJsonObject(mapOf("today" to TODAY_RANGE))
        m.map = mapOf("a0" to 0, "b1" to 1)
        m.array = listOf(0.0, 1.0, 2.0, 3.0)

        trans.submit()
    }

}