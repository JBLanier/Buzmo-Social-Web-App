package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.MessageInABottle;
import edu.ucsb.engineering.buzmo.api.*;
import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@PermitAll
@Path("/chatgroups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatGroupsResource {

    private static final int LIST_LIMIT = 100;
    private static final int CONV_LIMIT = 100;

    private ChatGroupsDAO dao;

    public ChatGroupsResource(ChatGroupsDAO dao) {
        this.dao = dao;
    }

    @Path("/list")
    @GET
    public List<ConversationListItem> getList(@Context SecurityContext ctxt) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        //pagination later.
        return this.dao.getConversationList(user.getUserid(), LIST_LIMIT, 0);
    }

    @Path("/conversation")
    @GET
    public List<Message> getConversation(@QueryParam("cgid") long cgid,
                                         @QueryParam("offset") int offset) throws SQLException {
        return this.dao.getConversation(cgid, CONV_LIMIT, offset);
    }

    @Path("/conversation/delete")
    @POST
    public void deleteMessage(@QueryParam("mid") long mid) throws SQLException {
        this.dao.markDeleted(mid);
    }

    @Path("/conversation/send")
    @POST
    public void sendMessage(@Context SecurityContext ctxt, MessageInABottle msg) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        this.dao.sendMessage(user.getUserid(), msg.getRecipient(), (new Date()).getTime(), msg.getMsg());
    }

    @GET
    public ChatGroup getChatGroup(@QueryParam("cgid") long cgid) throws SQLException {
        return this.dao.getChatGroup(cgid);
    }

    @Path("/invite/create")
    @POST
    public Response createChatGroupInvite(ChatGroupInvite inv) {
        try {
            ///TODO: need to change time to simulation time
            dao.sendInvite(inv.getCgid(), inv.getSender(), inv.getRecipient(), inv.getMsg(), new Date().getTime());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Path("/request/respond")
    @POST
    public Response respondToInvite(@QueryParam("cgid") long cgid, @QueryParam("userid") long userid,
                                     @QueryParam("accept") boolean accept) {
        try {
            dao.respondToInvite(cgid,userid,accept);
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Path("/checkmembership")
    @POST
    public Response checkMembership(@QueryParam("cgid") long cgid, @QueryParam("userid") long userid) throws SQLException {

            boolean result = dao.checkMembership(cgid,userid);
        if (result) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

}
