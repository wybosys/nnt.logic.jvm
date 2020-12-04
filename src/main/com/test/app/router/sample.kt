package com.test.app.router

import com.nnt.component.TODAY_RANGE
import com.nnt.core.*
import com.nnt.manager.Dbms
import com.nnt.store.*
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

    @action(Null::class)
    suspend fun mysql(trans: Trans) {
        trans.submit()

        val mysql = Dbms.Find("mysql") as RMysql

        mysql.tables()
        mysql.table("echoo")
    }

    @action(Null::class)
    suspend fun phoenix(trans: Trans) {
        trans.submit()

        var cur = 0
        val phoenix = Dbms.Find("phoenix") as Phoenix
        val ses = phoenix.acquireSession() as PhoenixJdbcSession

        phoenix.schemes()
        phoenix.scheme("xaas")
        phoenix.tables("xaas")
        phoenix.table("nnt-sample", "test")

        // 测试时关闭自动维持连接
        ses.stopKeepAlive()

        // 创建测试表

        // 测试phoenix-queryserver链接可靠性
        Repeat(1) {
            cur += 1
        }

        ses.close()
    }

}