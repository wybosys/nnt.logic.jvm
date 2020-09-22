package com.nnt.test

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface ITest {

    @GET
    @Path("hello")
    fun hello(): String
}

class Test : ITest {//, TestGrpc.TestImplBase() {

    override fun hello(): String {
        return """["HELLO"]"""
    }

/*
    override fun hello(request: TestReq, responseObserver: StreamObserver<TestReply>) {
        val reply = TestReply.newBuilder().setMessage("hello").build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
 */
}