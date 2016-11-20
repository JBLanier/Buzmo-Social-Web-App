package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.ConversationListItem;
import edu.ucsb.engineering.buzmo.api.ConversationMessage;
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
                convos.add(new ConversationListItem(rs.getString("SCREENNAME"), rs.getLong("USERID"),
                        rs.getLong("UTC")));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return convos;
    }

    public List<ConversationMessage> getConversation(long cgid, int limit, int offset)
            throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ConversationMessage> msgs = new ArrayList<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM (\n" +
                    "  SELECT\n" +
                    "    U.SCREENNAME,\n" +
                    "    U.USERID,\n" +
                    "    M.MSG_TIMESTAMP AS UTC,\n" +
                    "    M.MSG\n" +
                    "  FROM CHAT_GROUP_MESSAGES G, USERS U, MESSAGES M\n" +
                    "  WHERE\n" +
                    "    G.CGID = ? AND\n" +
                    "    G.MID = M.MID AND\n" +
                    "    M.SENDER = U.USERID AND\n" +
                    "    M.IS_DELETED = 0\n" +
                    "  ORDER BY UTC DESC\n" +
                    ") WHERE\n" +
                    "    ROWNUM > ? AND\n" +
                    "    ROWNUM <= ? + ?");
            pstmt.setLong(1, cgid);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, offset);
            pstmt.setInt(4, limit);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                msgs.add(new ConversationMessage(rs.getString("SCREENNAME"), rs.getLong("USERID"),
                        rs.getString("MSG"), rs.getLong("UTC")));
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
}
