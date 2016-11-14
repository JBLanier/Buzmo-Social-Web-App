package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private DataSource ds;

    public UserDAO(DataSource ds) {
        this.ds = ds;
    }

    public User getUser(long userid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User toReturn = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT email, name, screename, phone, is_manager FROM users WHERE userid = ?");
            pstmt.setLong(1, userid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                toReturn = new User(rs.getLong("userid"),rs.getString("name"), rs.getString("email"),
                        rs.getString("screenname"), rs.getLong("phone"), (rs.getInt("is_manager") > 0));
            }
        } finally {
            if (rs != null) {
                try{
                    rs.close();
                } catch (SQLException e) {}
            }
            if (pstmt != null) {
                try{
                    pstmt.close();
                } catch (SQLException e) {}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }

        return toReturn;

    }
}
