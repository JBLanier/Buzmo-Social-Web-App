package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.api.UserCreationRequest;

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
            pstmt = conn.prepareStatement("SELECT userid, email, full_name, screenname, phone, is_manager " +
                    "FROM users WHERE userid = ?");
            pstmt.setLong(1, userid);
            rs = pstmt.executeQuery();
            //Get the first result, if one is found.
            if (rs.next()) {
                toReturn = new User(rs.getLong("userid"),rs.getString("full_name"), rs.getString("email"),
                        rs.getString("screenname"), rs.getLong("phone"), (rs.getInt("is_manager") > 0));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return toReturn;

    }

    public User getUser(String email) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User toReturn = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT userid, email, full_name, screenname, phone, is_manager " +
                    "FROM users WHERE EMAIL = ?");
            pstmt.setString(1,email);
            rs = pstmt.executeQuery();
            //Get the first result, if one is found.
            if (rs.next()) {
                toReturn = new User(rs.getLong("userid"),rs.getString("full_name"), rs.getString("email"),
                        rs.getString("screenname"), rs.getLong("phone"), (rs.getInt("is_manager") > 0));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return toReturn;

    }

    public User getLoginMatch(String email, String passwd) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User toReturn = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT U.USERID, U.FULL_NAME, U.EMAIL, U.SCREENNAME, U.PHONE, U.IS_MANAGER " +
                    "FROM USERS U WHERE U.EMAIL = ? AND U.PASSWD = ?");
            pstmt.setString(1,email);
            pstmt.setString(2,passwd);
            rs = pstmt.executeQuery();
            //Get the first result, if one is found.
            if (rs.next()) {

                toReturn = new User(rs.getLong("userid"),rs.getString("full_name"),rs.getString("email"),
                        rs.getString("screenname"),rs.getLong("phone"),rs.getInt("is_manager") > 0);
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return toReturn;

    }

    public void createUser(UserCreationRequest user) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String toReturn = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO USERS (USERID, EMAIL, FULL_NAME, PASSWD, PHONE, SCREENNAME, IS_MANAGER) " +
                    "VALUES (0,?,?,?,?,?,?)");
            pstmt.setString(1,user.getEmail());
            pstmt.setString(2,user.getName());
            pstmt.setString(3,user.getPasswd());
            pstmt.setLong(4,user.getPhone());
            pstmt.setString(5,user.getScreenname());
            pstmt.setInt(6, user.isManager() ? 1 : 0);
            pstmt.executeUpdate();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

    }

}
