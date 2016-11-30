package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.*;
import edu.ucsb.engineering.buzmo.daos.MyCircleDAO;
import edu.ucsb.engineering.buzmo.daos.UserDAO;
import edu.ucsb.engineering.buzmo.util.TimeKeeper;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Path("/mycircle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)



public class MyCircleResource {

    private static final int MC_FETCH_SIZE = 7;
    private static final int MC_SEARCH_LIMIT = 7;

    private MyCircleDAO dao;
    private UserDAO userDAO;
    private TimeKeeper tk;

    public MyCircleResource(MyCircleDAO dao, UserDAO userDAO, TimeKeeper tk) {
        this.dao = dao;
        this.userDAO = userDAO;
        this.tk = tk;
    }

    @Path("/list")
    @PermitAll
    @GET
    public Response getRequests(@Context SecurityContext ctxt, @QueryParam("before") Long before) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        List<MyCircleMessage> msgs = null;
        msgs = dao.getMessages(user.getUserid(),before, MC_FETCH_SIZE);
        return Response.ok(msgs).build();
    }

    @Path("/search/any")
    @PermitAll
    @POST
    public Response searchAny(@Context SecurityContext ctxt, MC_MSG_Search query) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        //If no topics, use user topics.
        if (query.getTopics().size() == 0) {
            query.setTopics(this.userDAO.getTopics(user.getUserid()));
        }
        List<MyCircleMessage> msgs = null;
        msgs = dao.searchAtLeastTopics(query, MC_SEARCH_LIMIT);
        return Response.ok(msgs).build();
    }

    @Path("/search/all")
    @PermitAll
    @POST
    public Response searchALL(@Context SecurityContext ctxt, MC_MSG_Search query) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        //If no topics, use user topics.
        if (query.getTopics().size() == 0) {
            query.setTopics(this.userDAO.getTopics(user.getUserid()));
        }
        List<MyCircleMessage> msgs = null;
        msgs = dao.searchAllTopics(query, MC_SEARCH_LIMIT);
        return Response.ok(msgs).build();
    }

    @Path("/create")
    @PermitAll
    @POST
    public Response createMessage(@Context SecurityContext ctxt, MyCircleMessageCreationRequest msg) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        msg.setUserid(user.getUserid());

        if (msg.getTopics() == null || msg.getTopics().size() == 0) {
            //use the user's topics
            msg.setTopics(this.userDAO.getTopics(user.getUserid()));
        }

        msg.setUtc(tk.getTime()); //override client
        dao.createMyCircleMessage(msg);
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