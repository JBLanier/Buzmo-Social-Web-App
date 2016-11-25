package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.FriendRequest;
import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.daos.FriendsDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Path("/friends")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendsResource {

    private FriendsDAO dao;

    public FriendsResource(FriendsDAO ds) {this.dao = ds;}

    @Path("/list")
    @GET
    public Response getFriendsList(@QueryParam("userid") long userid) throws SQLException {
        List<User> fl = null;

        fl = dao.getFriendsList(userid);

        return Response.ok(fl).build();
    }

    @Path("/requests")
    @GET
    public Response getRequests(@QueryParam("userid") long userid) throws SQLException {
        List<FriendRequest> fr = null;

           fr = dao.getRequests(userid);

        return Response.ok(fr).build();
    }

    @Path("/request/create")
    @POST
    public Response createRequest(FriendRequest fr){
        try {
            ///TODO: need to change time to simulation time
            dao.createRequest(fr, new Date().getTime());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
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
}