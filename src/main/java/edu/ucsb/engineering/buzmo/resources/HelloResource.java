package edu.ucsb.engineering.buzmo.resources;

import jersey.repackaged.com.google.common.base.Optional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloResource {
    @GET
    public String sayHello(@QueryParam("name") String name) {
        if (name != null) {
            return String.format("Hi %s!", name);
        } else {
            return "Hi there!";
        }
    }
}
