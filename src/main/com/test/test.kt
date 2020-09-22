package com.test

import com.nnt.test.TestGrpc
import com.nnt.test.TestReply
import com.nnt.test.TestReq
import io.grpc.stub.StreamObserver
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

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
        return """["HELLO"]"""
    }

    // grpc协议下必须实现该函数
    fun setProxiedImpl(impl: ITest) {
        super.setProxiedImpl(impl)
    }

    override fun hello(request: TestReq, responseObserver: StreamObserver<TestReply>) {
        val reply = TestReply.newBuilder().setMessage("hello grpc").build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}