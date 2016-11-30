package edu.ucsb.engineering.buzmo.resources;


import edu.ucsb.engineering.buzmo.util.TimeKeeper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/time")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeResource {
    private TimeKeeper tk;

    public TimeResource(TimeKeeper tk) {
        this.tk = tk;
    }

    @Path("/set")
    @GET
    public void setTime(@QueryParam("utc") long utc) {
        this.tk.setTime(utc);
    }

    @Path("/get")
    @GET
    public long getTime() {
        return this.tk.getTime();
    }

}
