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
            if (rs.next()) {
                toReturn.add(new FriendRequest(rs.getLong("MID"),rs.getLong("RECIPIENT"),rs.getString("MSG"),
                        rs.getLong("MSG_TIMESTAMP"), rs.getLong("SENDER"), rs.getString("SCREENNAME")));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return toReturn;

    }
}
