package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.FriendRequest;
import edu.ucsb.engineering.buzmo.api.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class FriendsDAO {
    private DataSource ds;

    public FriendsDAO(DataSource ds) {
        this.ds = ds;
    }

    public List<User> getFriendsList(Long userid) throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> toReturn = new ArrayList<>();
        try {
            conn = this.ds.getConnection();


//            public User(@JsonProperty("userid") long userid, @JsonProperty("full_name") String full_name,
//            @JsonProperty("email") String email, @JsonProperty("screenname") String screenname,
//            @JsonProperty("phone") long phone, @JsonProperty("isManager") boolean is_manager) {

            pstmt = conn.prepareStatement("" +
                    "SELECT U.USERID, U.SCREENNAME, U.EMAIL, U.PHONE, U.IS_MANAGER " +
                    "FROM USERS U, FRIENDS F " +
                    "WHERE (F.U1 = U.USERID AND F.U2 = ?) OR (F.U2 = U.USERID AND F.U1 = ?)");
            pstmt.setLong(1, userid);
            pstmt.setLong(2, userid);
            rs = pstmt.executeQuery();
            //Get the first result, if one is found.
            while (rs.next()) {
                toReturn.add(new User(rs.getLong("userid"),null,rs.getString("email"),
                        rs.getString("screenname"),rs.getLong("phone"),rs.getBoolean("is_manager")));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());}
        }

        return toReturn;
    }

    public List<FriendRequest> getRequests(long userid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<FriendRequest> toReturn = new ArrayList<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("" +
                    "SELECT M.MID, M.MSG, M.MSG_TIMESTAMP, M.SENDER, F.RECIPIENT, U.SCREENNAME " +
                    "FROM MESSAGES M, FRIEND_REQUESTS F, USERS U " +
                    "WHERE M.MID = F.MID AND " +
                    "U.USERID = M.SENDER AND " +
                    "F.RECIPIENT = ?");
            pstmt.setLong(1, userid);
            rs = pstmt.executeQuery();
            //Get the first result, if one is found.
            while (rs.next()) {
                toReturn.add(new FriendRequest(rs.getLong("MID"),rs.getLong("RECIPIENT"),rs.getString("MSG"),
                        rs.getLong("MSG_TIMESTAMP"), rs.getLong("SENDER"), rs.getString("SCREENNAME")));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());}
        }

        return toReturn;

    }

    public void createRequest(FriendRequest fr, long utc) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        try {
            conn = this.ds.getConnection();
            String generatedColumns[] = {"MID"};
            pstmt = conn.prepareStatement("INSERT INTO MESSAGES(SENDER,MSG,MSG_TIMESTAMP,IS_DELETED) VALUES (?,?,?,?)",
                    generatedColumns);
            pstmt.setLong(1, fr.getSender());
            pstmt.setString(2, fr.getMsg());
            pstmt.setLong(3, utc);
            pstmt.setInt(4, 0); //not deleted by default
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            //Get the first result, if one is found.
            if (rs.next()) {
                //Insert into private messages.
                long mid = rs.getLong(1);
                pstmt2 = conn.prepareStatement("INSERT INTO FRIEND_REQUESTS(MID, RECIPIENT) VALUES (?,?)");
                pstmt2.setLong(1, mid);
                pstmt2.setLong(2, fr.getRecipient());
                pstmt2.executeUpdate();
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());}
        }
    }

    public void respondToRequest(long mid, boolean accept) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        ResultSet rs = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT F.RECIPIENT, M.SENDER " +
                                            "FROM MESSAGES M, FRIEND_REQUESTS F " +
                                            "WHERE F.MID=M.MID AND M.MID = ?");
            pstmt.setLong(1, mid);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            //check the first result, if one is found.
            //If the friend request exists carry out the response

            if (rs.next()) {

                if (accept) {
                    //Create friendship
                    long u1 = rs.getLong(1);
                    long u2 = rs.getLong(2);
                    pstmt2 = conn.prepareStatement("INSERT INTO FRIENDS VALUES (?,?)");
                    pstmt2.setLong(1, u1);
                    pstmt2.setLong(2, u2);
                    pstmt2.executeUpdate();
                }
                pstmt3 = conn.prepareStatement("DELETE FROM MESSAGES M WHERE M.MID = ?");
                pstmt3.setLong(1, mid);
                pstmt3.executeUpdate();

            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt3 != null) pstmt3.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());}
        }
    }
}
