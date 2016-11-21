package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.FriendRequest;
import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.daos.FriendsDAO;
import edu.ucsb.engineering.buzmo.daos.UserDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
        }

        return Response.ok(user).build();
    }
}