package com.nnt.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test2")
public interface ITest2 {

    @GET
    @Path("hello")
    public void hello();
}
