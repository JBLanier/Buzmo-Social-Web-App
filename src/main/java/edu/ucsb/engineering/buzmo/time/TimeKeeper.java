package edu.ucsb.engineering.buzmo.time;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class TimeKeeper {
    private long start; //game time start
    private long upStart; //system clock to compute uptime
    private BasicDataSource ds;

    public TimeKeeper(BasicDataSource ds) {
        this.ds = ds;
        this.upStart = (new Date()).getTime();
        this.loadTime();
    }

    public long getTime() {
        return this.start + ((new Date()).getTime() - this.upStart);
    }

    public void setTime(long utc) {
        this.start = utc;
        this.upStart = (new Date()).getTime();
        this.saveTime();
    }

    public void saveTime() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("UPDATE GAME_TIME SET UTC = ? WHERE GTID = 1");
            pstmt.setLong(1, this.getTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());}
        }
    }

    public void loadTime() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long utc = (new Date()).getTime();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT UTC FROM GAME_TIME WHERE GTID = 1");
            rs = pstmt.executeQuery();
            if(rs.next()) {
                utc = rs.getLong(1);
            } else {
                System.err.println("Could not load time from DB!!!");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());}
        }
        this.start = utc;
        this.upStart = (new Date()).getTime();
    }
}
