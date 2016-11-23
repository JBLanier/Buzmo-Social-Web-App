package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.*;
import edu.ucsb.engineering.buzmo.daos.MyCircleDAO;
import edu.ucsb.engineering.buzmo.daos.UserDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Path("/mycircle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)



public class MyCircleResource {

    private static final int MC_MESSAGE_REQUEST_LIMIT = 100;
    private static final int MC_SEARCH_LIMIT = 100;

    private MyCircleDAO dao;

    public MyCircleResource(MyCircleDAO ds) {this.dao = ds;}

    @Path("/list")
    @GET
    public Response getRequests(@QueryParam("userid") long userid, @QueryParam("offset") int offset) throws SQLException {
        List<MyCircleMessage> msgs = null;
        msgs = dao.getMessages(userid,offset,MC_MESSAGE_REQUEST_LIMIT);
        return Response.ok(msgs).build();
    }

    @Path("/search/any")
    @GET
    public Response searchAny(MC_MSG_Search query) throws SQLException {
        List<MyCircleMessage> msgs = null;
        msgs = dao.searchAtLeastTopics(query, MC_SEARCH_LIMIT);
        return Response.ok(msgs).build();
    }

    @Path("/search/all")
    @GET
    public Response searchALL(MC_MSG_Search query) throws SQLException {
        List<MyCircleMessage> msgs = null;
        msgs = dao.searchAllTopics(query, MC_SEARCH_LIMIT);
        return Response.ok(msgs).build();
    }

    @Path("/create")
    @POST
    public Response createMessage(MyCircleMessageCreationRequest msg) {
        try {
            dao.createMyCircleMessage(msg);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.ACCEPTED).build();

    }

    @Path("/delete")
    @POST
    public Response deleteMessage(@QueryParam("mid") long mid) {
        try {
            dao.markforDeletion(mid);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.ACCEPTED).build();

    }

}