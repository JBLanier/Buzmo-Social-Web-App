package edu.ucsb.engineering.buzmo.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloResource {
    @Path("/hi")
    @GET
    public String sayHello(@QueryParam("name") String name) {
        if (name != null) {
            return String.format("Hi %s!", name);
        } else {
            return "Hi there!";
        }
    }

    @Path("/host")
    @GET
    public String sayHost(@Context HttpServletRequest request) {
        return String.format("You are connecting to %s!", request.getServerName());
    }
}
