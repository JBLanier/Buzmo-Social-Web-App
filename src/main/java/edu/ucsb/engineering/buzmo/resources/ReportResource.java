package edu.ucsb.engineering.buzmo.resources;

import edu.ucsb.engineering.buzmo.api.Report;
import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;
import edu.ucsb.engineering.buzmo.daos.ReportDAO;
import edu.ucsb.engineering.buzmo.time.TimeKeeper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("/report")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
    private TimeKeeper tk;
    private ReportDAO dao;

    private static final long SEVENDAYSINMILLISECONDS = 604800000;

    public ReportResource(TimeKeeper tk, ReportDAO reportDAO) {
        this.tk = tk;
        this.dao = reportDAO;
    }



    @GET
    public Report getTime() throws SQLException {

        return this.dao.getReport(tk.getTime()-SEVENDAYSINMILLISECONDS, tk.getTime());

    }

}