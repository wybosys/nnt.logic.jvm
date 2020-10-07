package com.test.dubbo

import com.google.protobuf.Empty
import com.nnt.core.logger
import com.nnt.manager.Dbms
import com.nnt.store.RMysql
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

interface Sample {
    fun listEchoo(): List<Echoo>
}

class Echoo {
    var id: Int = 0
    var input: String = ""
    var output: String = ""

    override fun toString(): String {
        return "${id} ${input} ${output}"
    }
}

@Path("test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface ITest : TestGrpc.ITest {

    @GET
    @Path("hello")
    fun hello(): String
}

class Test : ITest, TestGrpc.TestImplBase() {

    override fun hello(): String {
        logger.info("调用 rest-hello")

        return """["HELLO"]"""
    }

    // grpc协议下必须实现该函数
    fun setProxiedImpl(impl: ITest) {
        super.setProxiedImpl(impl)
    }

    override fun hello(request: Empty, responseObserver: StreamObserver<TestReply>) {
        logger.info("调用 grpc-hello")

        GlobalScope.launch {
            val mysql = Dbms.Find("mysql") as RMysql
            mysql.execute {
                val map = it.getMapper(Sample::class.java)
                val res = map.listEchoo()
                println(res)
            }

            val reply = TestReply.newBuilder().setMessage("hello grpc").build()
            responseObserver.onNext(reply)
            responseObserver.onCompleted()
        }
    }
}