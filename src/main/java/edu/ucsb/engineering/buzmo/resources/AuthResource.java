package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.LoginRequest;
import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.auth.SessionManager;
import edu.ucsb.engineering.buzmo.daos.UserDAO;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private SessionManager sm;
    private UserDAO userDAO;

    public AuthResource(SessionManager sm, UserDAO userDAO) {
        this.sm = sm;
        this.userDAO = userDAO;
    }

    @Path("/login")
    @POST
    public Response login(LoginRequest login) throws SQLException {
        User user = userDAO.getLoginMatch(login.getEmail(),login.getPassword());
        if (user != null) {
            String token = this.sm.startSession(user);
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
