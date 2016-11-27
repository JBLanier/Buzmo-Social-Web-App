package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.daos.UserDAO;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserDAO dao;

    public UserResource(UserDAO ds) {this.dao = ds;}

    @GET
    public Response getUser(@QueryParam("userid") Long userid, @QueryParam("email") String email) throws SQLException {
        User user = null;

        if (userid != null) {
            user = dao.getUser(userid);
        } else if (email != null) {
            user = dao.getUser(email);
        }

        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @Path("/profile")
    @GET
    @PermitAll
    public Response check(@Context SecurityContext ctxt) {
        User user = ((User) ctxt.getUserPrincipal());
        return Response.ok(user).build();
    }
}