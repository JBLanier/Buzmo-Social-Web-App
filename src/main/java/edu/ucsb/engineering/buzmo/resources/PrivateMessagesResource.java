package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.MessageInABottle;
import edu.ucsb.engineering.buzmo.api.ConversationListItem;
import edu.ucsb.engineering.buzmo.api.Message;
import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.daos.PrivateMessageDAO;
import edu.ucsb.engineering.buzmo.util.TimeKeeper;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@PermitAll
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrivateMessagesResource {

    private static final int LIST_LIMIT = 100;
    private static final int CONV_LIMIT = 7;

    private PrivateMessageDAO dao;
    private TimeKeeper tk;

    public PrivateMessagesResource(PrivateMessageDAO dao, TimeKeeper tk) {
        this.dao = dao;
        this.tk = tk;
    }

    @Path("/list")
    @GET
    public List<ConversationListItem> getList(@Context SecurityContext ctxt) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        //If we want to do pagination, we can do it later.
        return this.dao.getConversationList(user.getUserid(), LIST_LIMIT, 0);
    }

    @Path("/conversation")
    @GET
    public List<Message> getConversation(@Context SecurityContext ctxt, @QueryParam("before") Long before,
                                         @QueryParam("user") long other) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        //If we want to do pagination, we can do it later.
        return this.dao.getConversation(user.getUserid(), other, CONV_LIMIT, before);
    }

    @Path("/delete")
    @POST
    public void deleteMessage(@Context SecurityContext ctxt, @QueryParam("mid") long mid) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        //If we want to do pagination, we can do it later.
        this.dao.markDeleted(user.getUserid(), mid);
    }

    @Path("/send")
    @POST
    public void sendMessages(@Context SecurityContext ctxt, MessageInABottle msg) throws SQLException {
        User user = (User) ctxt.getUserPrincipal();
        //If we want to do pagination, we can do it later.
        this.dao.sendMessage(user.getUserid(), msg.getRecipient(), msg.getMsg(), tk.getTime());
    }

}
