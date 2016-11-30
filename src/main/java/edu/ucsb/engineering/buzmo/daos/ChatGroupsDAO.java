package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.ChatGroup;
import edu.ucsb.engineering.buzmo.api.ConversationListItem;
import edu.ucsb.engineering.buzmo.api.Message;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jb on 11/13/16.
 */
public class ChatGroupsDAO {

    private BasicDataSource ds;

    public ChatGroupsDAO(BasicDataSource ds) {
        this.ds = ds;
    }

//    public List<ConversationListItem> getConversationList(long userid, int limit, int offset) throws SQLException {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        List<ConversationListItem> convos = new ArrayList<>();
//        try {
//            conn = this.ds.getConnection();
//            pstmt = conn.prepareStatement("SELECT * FROM (\n" +
//                    "  SELECT D.CGID, D.GROUP_NAME, UTC FROM (\n" +
//                    "    SELECT\n" +
//                    "      C.CGID,\n" +
//                    "      MAX(M.MSG_TIMESTAMP) AS UTC\n" +
//                    "    FROM CHAT_GROUPS C, CHAT_GROUP_MESSAGES S, MESSAGES M\n" +
//                    "    WHERE\n" +
//                    "      C.CGID = S.CGID AND\n" +
//                    "      S.MID = M.MID AND\n" +
//                    "      M.IS_DELETED = 0\n" +
//                    "    GROUP BY C.CGID\n" +
//                    "  ) F, CHAT_GROUPS D\n" +
//                    "  WHERE\n" +
//                    "    F.CGID = D.CGID AND\n" +
//                    "    F.CGID IN (\n" +
//                    "      /* chat group ids of groups user is member of */\n" +
//                    "      SELECT X.CGID FROM CHAT_GROUP_MEMBERS X\n" +
//                    "      WHERE X.USERID = ?\n" +
//                    "    )\n" +
//                    "  ORDER BY UTC DESC\n" +
//                    ") WHERE\n" +
//                    "    ROWNUM > ? AND\n" +
//                    "    ROWNUM <= ? + ?");
//            pstmt.setLong(1, userid);
//            pstmt.setInt(2, offset);
//            pstmt.setInt(3, offset);
//            pstmt.setInt(4, limit);
//
//            rs = pstmt.executeQuery();
//            //Get the first result, if one is found.
//            if (rs.next()) {
//                convos.add(new ConversationListItem(rs.getString("GROUP_NAME"), rs.getLong("CGID"),
//                        rs.getLong("UTC")));
//            }
//        } finally {
//            try { if (rs != null) rs.close(); } catch (Exception e) {}
//            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
//            try { if (conn != null) conn.close(); } catch (Exception e) {}
//        }
//        return convos;
//    }

    public List<ConversationListItem> getConversationList(long userid, int limit, int offset) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ConversationListItem> convos = new ArrayList<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM (\n" +
                    "  SELECT D.CGID, D.GROUP_NAME, UTC FROM (\n" +
                    "    SELECT\n" +
                    "      C.CGID,\n" +
                    "      MAX(M.MSG_TIMESTAMP) AS UTC\n" +
                    "    FROM CHAT_GROUPS C, CHAT_GROUP_MESSAGES S, MESSAGES M\n" +
                    "    WHERE\n" +
                    "      C.CGID = S.CGID AND\n" +
                    "      S.MID = M.MID AND\n" +
                    "      M.IS_DELETED = 0\n" +
                    "    GROUP BY C.CGID\n" +
                    "  ) F, CHAT_GROUPS D\n" +
                    "  WHERE\n" +
                    "    F.CGID = D.CGID AND\n" +
                    "    F.CGID IN (\n" +
                    "      /* chat group ids of groups user is member of */\n" +
                    "      SELECT X.CGID FROM CHAT_GROUP_MEMBERS X\n" +
                    "      WHERE X.USERID = ?\n" +
                    "    )\n" +
                    "  ORDER BY UTC DESC\n" +
                    ") WHERE\n" +
                    "    ROWNUM > ? AND\n" +
                    "    ROWNUM <= ? + ?");
            pstmt.setLong(1, userid);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, offset);
            pstmt.setInt(4, limit);

            rs = pstmt.executeQuery();
            //Get the first result, if one is found.
            if (rs.next()) {
                convos.add(new ConversationListItem(rs.getString("GROUP_NAME"), rs.getLong("CGID"),
                        rs.getLong("UTC"),false));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return convos;
    }

    public List<Message> getConversation(long cgid, int limit, Long before)
            throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Message> msgs = new ArrayList<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement(
                "SELECT * FROM (\n" +
                        "SELECT * FROM (\n" +
                            "  SELECT\n" +
                            "    U.SCREENNAME,\n" +
                            "    U.USERID,\n" +
                            "    M.MSG_TIMESTAMP AS UTC,\n" +
                            "    M.MSG,\n" +
                            "    M.MID\n" +
                            "  FROM CHAT_GROUP_MESSAGES G, USERS U, MESSAGES M\n" +
                            "  WHERE\n" +
                            "    G.CGID = ? AND\n" +
                            "    G.MID = M.MID AND\n" +
                            "    M.SENDER = U.USERID AND\n" +
                            "    M.IS_DELETED = 0)\n" +
                        ((before == null) ? "" : "WHERE UTC < ?\n") +
                        " ORDER BY UTC DESC \n" +
                        ")\n" +
                    "WHERE\n" +
                    "    ROWNUM <= ?\n" +
                    "ORDER BY UTC ASC\n");
            int i = 1;
            pstmt.setLong(i++, cgid);
            if (before != null) {
                pstmt.setLong(i++, before);
            }
            pstmt.setInt(i++, limit);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                msgs.add(new Message(rs.getString("SCREENNAME"), rs.getLong("USERID"),
                        rs.getString("MSG"), rs.getLong("UTC"), rs.getLong("MID")));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return msgs;
    }

    public void markDeleted(long mid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("UPDATE MESSAGES M SET M.IS_DELETED = 1 WHERE M.MID = ?");
            pstmt.setLong(1, mid);
            pstmt.executeUpdate();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public void sendMessage(long sender, long cgid, long utc, String msg) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        try {
            conn = this.ds.getConnection();
            String generatedColumns[] = {"MID"};
            pstmt = conn.prepareStatement("INSERT INTO MESSAGES(SENDER,MSG,MSG_TIMESTAMP,IS_DELETED) VALUES (?,?,?,?)",
                    generatedColumns);
            pstmt.setLong(1, sender);
            pstmt.setString(2, msg);
            pstmt.setLong(3, utc);
            pstmt.setInt(4, 0); //not deleted by default
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            //Get the first result, if one is found.
            if (rs.next()) {
                //Insert into private messages.
                long mid = rs.getLong(1);
                pstmt2 = conn.prepareStatement("INSERT INTO CHAT_GROUP_MESSAGES(MID, CGID) VALUES (?,?)");
                pstmt2.setLong(1, mid);
                pstmt2.setLong(2, cgid);
                pstmt2.executeUpdate();
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    //Returns meta data info about the group.
    public ChatGroup getChatGroup(long cgid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChatGroup cg = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM CHAT_GROUPS WHERE CGID = ?");
            pstmt.setLong(1, cgid);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            //Get the first result, if one is found.
            if (rs.next()) {
                cg = new ChatGroup(rs.getLong("CGID"), rs.getString("GROUP_NAME"), rs.getLong("DURATION"),
                        rs.getLong("OWNER"));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return cg;
    }

    public void sendInvite(long cgid, long sender, long recipient, String msg, long utc) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        try {
            conn = this.ds.getConnection();
            String generatedColumns[] = {"MID"};
            pstmt = conn.prepareStatement("INSERT INTO MESSAGES(SENDER,MSG,MSG_TIMESTAMP,IS_DELETED) VALUES (?,?,?,?)",
                    generatedColumns);
            pstmt.setLong(1, sender);
            pstmt.setString(2, msg);
            pstmt.setLong(3, utc);
            pstmt.setInt(4, 0); //not deleted by default
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            //Get the first result, if one is found.
            if (rs.next()) {
                //Insert into private messages.
                long mid = rs.getLong(1);
                pstmt2 = conn.prepareStatement("INSERT INTO CHAT_GROUP_INVITES(MID, CGID, RECIPIENT) VALUES (?,?,?)");
                pstmt2.setLong(1, mid);
                pstmt2.setLong(2, cgid);
                pstmt2.setLong(3, recipient);
                pstmt2.executeUpdate();
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public void respondToInvite(long cgid, long recipient, boolean accepted) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        try {
            conn = this.ds.getConnection();
            //DELETE invite
            pstmt = conn.prepareStatement("DELETE FROM MESSAGES M WHERE M.MID = (SELECT C.MID FROM CHAT_GROUP_INVITES " +
                    "C WHERE C.CGID = ? AND C.RECIPIENT = ?)");
            pstmt.setLong(1, cgid);
            pstmt.setLong(2, recipient);
            pstmt.executeUpdate();
            if (accepted) {
                //Add recipient to group.
                pstmt2 = conn.prepareStatement("INSERT INTO CHAT_GROUP_MEMBERS(USERID,CGID) VALUES (?,?)");
                pstmt2.setLong(1, recipient);
                pstmt2.setLong(2, cgid);
                pstmt2.executeUpdate();
            }
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    //create a group and add its owner to it.
    public void createGroup(long userid, String name, long duration) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        try {
            conn = this.ds.getConnection();
            String generatedColumns[] = {"CGID"};
            pstmt = conn.prepareStatement("INSERT INTO CHAT_GROUPS(NAME,DURATION,OWNER) VALUES (?,?,?)",
                    generatedColumns);
            pstmt.setString(1, name);
            pstmt.setLong(2, duration);
            pstmt.setLong(3, userid);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            //Get the first result, if one is found.
            if (rs.next()) {
                //Insert into private messages.
                long cgid = rs.getLong(1);
                pstmt2 = conn.prepareStatement("INSERT INTO CHAT_GROUP_MEMBERS(CGID, USERID) VALUES (?,?)");
                pstmt2.setLong(1, cgid);
                pstmt2.setLong(2, userid);
                pstmt2.executeUpdate();
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public void updateGroup(long cgid, String name, long duration, long owner) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("UPDATE CHAT_GROUPS C\n" +
                    "SET C.DURATION = ?, C.GROUP_NAME = ?, C.OWNER = ?\n" +
                    "WHERE C.CGID = ?");
            pstmt.setLong(1, duration);
            pstmt.setString(2, name);
            pstmt.setLong(3, owner);
            pstmt.setLong(4, cgid);
            pstmt.executeUpdate();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    //Deletes chat group messages, invites, and then the group itself.
    public void deleteGroup(long cgid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM MESSAGES M WHERE M.MID IN (\n" +
                            "  SELECT DISTINCT S.MID FROM CHAT_GROUP_MESSAGES S WHERE S.CGID = ?\n" +
                            "    UNION\n" +
                            "  SELECT DISTINCT P.MID FROM CHAT_GROUP_INVITES P WHERE P.CGID = ?\n" +
                            ")");
            pstmt.setLong(1, cgid);
            pstmt.setLong(2, cgid);
            pstmt.executeUpdate();
            pstmt2 = conn.prepareStatement("DELETE FROM CHAT_GROUPS WHERE CGID = ?");
            pstmt2.setLong(1, cgid);
            pstmt2.executeUpdate();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean checkMembership(long cgid, long userid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChatGroup cg = null;
        boolean inGroup = false;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM CHAT_GROUP_MEMBERS M " +
                    "WHERE M.CGID = ? AND " +
                    "M.USERID = ? ");
            pstmt.setLong(1, cgid);
            pstmt.setLong(2, userid);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            //Get the first result, if one is found.
            if (rs.next()) {
               inGroup = true;
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return inGroup;
    }

    public boolean checkOwnership(long cgid, long userid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean owner = false;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM CHAT_GROUPS C " +
                    "WHERE C.CGID = ? AND " +
                    "C.OWNER = ? ");
            pstmt.setLong(1, cgid);
            pstmt.setLong(2, userid);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            //Get the first result, if one is found.
            if (rs.next()) {
                owner = true;
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return owner;
    }

}
