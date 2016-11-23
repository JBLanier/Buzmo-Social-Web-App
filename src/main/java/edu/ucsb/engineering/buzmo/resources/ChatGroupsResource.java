package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.MessageInABottle;
import edu.ucsb.engineering.buzmo.api.ChatGroup;
import edu.ucsb.engineering.buzmo.api.ConversationListItem;
import edu.ucsb.engineering.buzmo.api.Message;
import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    private static final int CONV_LIMIT = 7;

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
    @GET
    public void deleteMessage(@QueryParam("mid") long mid) throws SQLException {
        this.dao.markDeleted(mid);
    }

    @Path("/conversation/send")
    @GET
    public void sendMessage(@Context SecurityContext ctxt, MessageInABottle msg) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        this.dao.sendMessage(user.getUserid(), msg.getRecipient(), (new Date()).getTime(), msg.getMsg());
    }

    @GET
    public ChatGroup getChatGroup(@QueryParam("cgid") long cgid) throws SQLException {
        return this.dao.getChatGroup(cgid);
    }

}
