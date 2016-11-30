package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.FriendRequest;
import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.api.UserSearch;
import edu.ucsb.engineering.buzmo.daos.AlreadyRequest;
import edu.ucsb.engineering.buzmo.daos.FriendsDAO;
import edu.ucsb.engineering.buzmo.daos.UserDAO;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Path("/friends")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendsResource {

    private FriendsDAO dao;
    private UserDAO userDAO;

    public FriendsResource(FriendsDAO ds, UserDAO userDAO) {this.dao = ds;this.userDAO=userDAO;}

    @Path("/list")
    @PermitAll
    @GET
    public Response getFriendsList(@Context SecurityContext ctxt) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        List<User> fl = null;

        fl = dao.getFriendsList(user.getUserid());

        return Response.ok(fl).build();
    }

    @Path("/requests")
    @PermitAll
    @GET
    public Response getRequests(@Context SecurityContext ctxt) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        List<FriendRequest> fr = null;

        fr = dao.getRequests(user.getUserid());

        return Response.ok(fr).build();
    }

    @Path("/request/create")
    @PermitAll
    @POST
    public Response createRequest(@Context SecurityContext ctxt, @QueryParam("other") long other) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        FriendRequest fr = new FriendRequest(0,other, "Please be my friend.",
                (new Date()).getTime(), user.getUserid(), user.getScreenname());
        try {
            ///TODO: need to change time to simulation time
            dao.createRequest(fr);
        } catch (AlreadyRequest e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Path("/request/respond")
    @POST
    public Response respondToRequest(@QueryParam("mid") long mid, @QueryParam("accept") boolean accept){
        try {
            dao.respondToRequest(mid,accept);
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Path("/search")
    @POST
    public List<User> searchUsers(UserSearch search) throws SQLException {
        return this.userDAO.searchUsers(search, 0, 7);
    }
}