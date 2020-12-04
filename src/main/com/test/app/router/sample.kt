package com.test.app.router

import com.nnt.component.TODAY_RANGE
import com.nnt.core.*
import com.nnt.manager.Dbms
import com.nnt.store.*
import com.test.app.model.Echoo
import com.test.app.model.Trans
import kotlin.random.Random

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

        // 测试时关闭自动维持连接
        ses.stopKeepAlive()

        if (phoenix.table("test") != null) {
            ses.execute("drop table ${ses.scheme}.test")
        }

        // 创建测试表
        ses.execute("create table ${ses.scheme}.test (id integer primary key, random_value integer)")

        // 测试phoenix-queryserver链接可靠性
        Repeat(1) {

            // 写入测试数据
            val cnt = ses.queryForObject(
                "select count(*) from ${ses.scheme}.test",
                Long::class
            )!!

            ses.update(
                "upsert into ${ses.scheme}.test (id, random_value) values (?, ?)",
                cur++, Random.nextInt(0, 10000000)
            )

            val cnt2 = ses.queryForObject(
                "select count(*) from ${ses.scheme}.test",
                Long::class
            )!!

            if (cnt != cnt2 - 1) {
                logger.fatal("写入phoenix数据失败 ${cur}")
            } else {
                logger.log("写入phoenix成功 ${cur}")
            }
        }
    }

}