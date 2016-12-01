package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.MessageInABottle;
import edu.ucsb.engineering.buzmo.api.*;
import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;
import edu.ucsb.engineering.buzmo.time.TimeKeeper;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@PermitAll
@Path("/chatgroups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatGroupsResource {

    private static final int LIST_LIMIT = 100;
    private static final int CONV_LIMIT = 5;

    private ChatGroupsDAO dao;
    private TimeKeeper tk;

    public ChatGroupsResource(ChatGroupsDAO dao, TimeKeeper tk) {
        this.dao = dao;
        this.tk = tk;
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
                                         @QueryParam("before") Long before) throws SQLException {
        return this.dao.getConversation(cgid, CONV_LIMIT, before);
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
        this.dao.sendMessage(user.getUserid(), msg.getRecipient(), tk.getTime(), msg.getMsg());
    }

    @GET
    public ChatGroup getChatGroup(@QueryParam("cgid") long cgid) throws SQLException {
        return this.dao.getChatGroup(cgid);
    }

    @Path("/invite/create")
    @PermitAll
    @POST
    public Response createChatGroupInvite(@Context SecurityContext ctxt, ChatGroupInvite inv) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        ///TODO: need to change time to simulation time
        dao.sendInvite(inv.getCgid(), user.getUserid(), inv.getRecipient(), inv.getMsg(), tk.getTime());
        return Response.status(Response.Status.OK).build();
    }

    @Path("/invite/respond")
    @POST
    public Response respondToInvite(@Context SecurityContext ctxt, @QueryParam("cgid") long cgid,
                                     @QueryParam("accept") boolean accept) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        dao.respondToInvite(cgid,user.getUserid(),accept);
        return Response.status(Response.Status.OK).build();
    }

    @Path("/invite/list")
    @GET
    public List<ChatGroupInviteResponse> listInvites(@Context SecurityContext ctxt) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        return this.dao.listInvites(user.getUserid());
    }

    @Path("/checkownerhip")
    @POST
    public Response checkOwnership(@QueryParam("cgid") long cgid,@Context SecurityContext ctxt) throws SQLException {

        User user = (User) ctxt.getUserPrincipal();
            boolean result = dao.checkOwnership(cgid,user.getUserid());
        if (result) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

    @Path("/create")
    @POST
    public Response createGroup(ChatGroup chatGroup) {
        try {
            dao.createGroup(chatGroup.getOwner(), chatGroup.getName(), chatGroup.getDuration());
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Path("/delete")
    @POST
    @PermitAll
    public Response deleteGroup(@Context SecurityContext ctxt, @QueryParam("cgid") long cgid) {
        User user = (User) ctxt.getUserPrincipal();

            try {
                if (dao.checkOwnership(cgid,user.getUserid())) {
                    dao.deleteGroup(cgid);
                } else {
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } catch (SQLException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.OK).build();

    }

    @Path("/update")
    @POST
    @PermitAll
    public Response updateGroup(@Context SecurityContext ctxt, ChatGroup chatGroup) {
        User user = (User) ctxt.getUserPrincipal();

        try {
            if (dao.checkOwnership(chatGroup.getCgid(),user.getUserid())) {
                dao.updateGroup(chatGroup.getCgid(),chatGroup.getName(),chatGroup.getDuration(),user.getUserid());
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();

    }
}
