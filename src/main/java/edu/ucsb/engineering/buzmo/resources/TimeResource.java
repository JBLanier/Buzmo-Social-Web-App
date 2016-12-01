package edu.ucsb.engineering.buzmo.resources;


import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;
import edu.ucsb.engineering.buzmo.time.TimeKeeper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("/time")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeResource {
    private TimeKeeper tk;
    private ChatGroupsDAO chatGroupsDAO;

    public TimeResource(TimeKeeper tk, ChatGroupsDAO chatGroupsDAO) {
        this.tk = tk;
        this.chatGroupsDAO = chatGroupsDAO;
    }

    @Path("/set")
    @POST
    public void setTime(@QueryParam("utc") long utc) {
        this.tk.setTime(utc);
        try {
            this.chatGroupsDAO.cleanup(this.tk.getTime());
        } catch (SQLException e) {
            System.err.println("Couldn't cleanup properly when time was set.");
        }
    }

    @Path("/get")
    @GET
    public long getTime() {
        return this.tk.getTime();
    }

}
