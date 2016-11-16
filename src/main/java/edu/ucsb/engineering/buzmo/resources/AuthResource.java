package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.LoginRequest;
import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.auth.SessionManager;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private SessionManager sm;

    public AuthResource(SessionManager sm) {
        this.sm = sm;
    }

    @Path("/login")
    @POST
    public Response login(LoginRequest login) {
        if (login.getPassword().equals("pass")) {
            String token = this.sm.startSession(new User(1, "Bob", "bob@bob.com", "bob23", 123L, false));
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).cookie(new NewCookie(
                    new Cookie("auth_token", token, "/", "localhost"))).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON)
                    .entity("Unauthorized").build();
        }
    }

    @Path("/check")
    @GET
    @PermitAll
    public String check(@Context SecurityContext ctxt) {
        return String.format("Hello there %s!", ((User) ctxt.getUserPrincipal()).getEmail());
    }
}
