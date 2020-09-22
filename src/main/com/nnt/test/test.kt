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
    @Path("test")
    fun Test()
}

class TestImpl : ITest {

    override fun Test() {
        println("HAHA")
    }
}