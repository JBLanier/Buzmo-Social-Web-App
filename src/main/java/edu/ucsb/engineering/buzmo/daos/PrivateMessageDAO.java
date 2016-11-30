package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.ConversationListItem;
import edu.ucsb.engineering.buzmo.api.Message;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrivateMessageDAO {

    private BasicDataSource ds;

    public PrivateMessageDAO(BasicDataSource ds) {
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
                    "  SELECT\n" +
                    "    U.screenname,\n" +
                    "    U.userid,\n" +
                    "    ts\n" +
                    "  FROM (\n" +
                    "    SELECT\n" +
                    "      other,\n" +
                    "      MAX(ts) AS ts\n" +
                    "    FROM (\n" +
                    "      /* Messages to me. */\n" +
                    "      SELECT\n" +
                    "        M.sender             AS other,\n" +
                    "        MAX(M.msg_timestamp) AS ts\n" +
                    "      FROM messages M, private_messages P\n" +
                    "      WHERE\n" +
                    "        M.mid = P.mid AND\n" +
                    "        P.recipient = ? AND\n" +
                    "        P.del_by_recipient = 0\n" +
                    "      GROUP BY M.sender\n" +
                    "      UNION\n" +
                    "      /* Messages from me. */\n" +
                    "      SELECT\n" +
                    "        P.recipient          AS other,\n" +
                    "        MAX(M.msg_timestamp) AS ts\n" +
                    "      FROM messages M, private_messages P\n" +
                    "      WHERE\n" +
                    "        M.mid = P.mid AND\n" +
                    "        M.sender = ? AND\n" +
                    "        M.is_deleted = 0\n" +
                    "      GROUP BY P.recipient\n" +
                    "    )\n" +
                    "    GROUP BY other\n" +
                    "    ), Users U\n" +
                    "  WHERE\n" +
                    "    U.userid = other\n" +
                    "\n" +
                    ") WHERE\n" +
                    "    ROWNUM > ? AND\n" +
                    "    ROWNUM <= ?+?");
            pstmt.setLong(1, userid);
            pstmt.setLong(2, userid);
            pstmt.setInt(3, offset);
            pstmt.setInt(4, offset);
            pstmt.setInt(5, limit);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                convos.add(new ConversationListItem(rs.getString("screenname"), rs.getLong("userid"),
                        rs.getLong("ts"),true));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return convos;
    }

    public List<Message> getConversation(long userid, long other, int limit, Long before) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Message> convos = new ArrayList<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement(
                "SELECT * FROM (\n" +
                        "SELECT * FROM (\n" +
                        "    SELECT M.MID, U.SCREENNAME, U.USERID, M.MSG, M.MSG_TIMESTAMP AS UTC\n" +
                        "    FROM USERS U, MESSAGES M, PRIVATE_MESSAGES P\n" +
                        "    WHERE\n" +
                        "        U.USERID = M.SENDER AND\n" +
                        "        M.MID = P.MID AND\n" +
                        "        (\n" +
                        "            (\n" +
                        "                /* requesting user is the sender */\n" +
                        "                M.SENDER = ? AND\n" +
                        "                P.RECIPIENT = ? AND\n" +
                        "                M.IS_DELETED = 0\n" +
                        "            ) OR (\n" +
                        "                /* requesting user is the recipient */\n" +
                        "                M.SENDER = ? AND\n" +
                        "                P.RECIPIENT = ? AND\n" +
                        "                P.DEL_BY_RECIPIENT = 0\n" +
                        "            )\n" +
                        "        ))\n" +
                        ((before == null) ? "" : "WHERE UTC < ?\n") +
                        " ORDER BY UTC DESC \n" +
                ")\n" +
                "WHERE\n" +
                "    ROWNUM <= ?\n" +
                "ORDER BY UTC ASC\n");
            int i = 1;
            pstmt.setLong(i++, userid);
            pstmt.setLong(i++, other);
            pstmt.setLong(i++, other);
            pstmt.setLong(i++, userid);
            if (before != null) {
                pstmt.setLong(i++, before);
            }
            pstmt.setInt(i++, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                convos.add(new Message(rs.getString("SCREENNAME"), rs.getLong("USERID"),
                        rs.getString("MSG"), rs.getLong("UTC"), rs.getLong("MID")));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return convos;
    }

    //userid is id of user who is deleting the private message
    public void markDeleted(long userid, long mid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("UPDATE MESSAGES M SET M.IS_DELETED = 1\n" +
                    "WHERE\n" +
                    "  M.MID = ? AND M.SENDER = ?");
            pstmt.setLong(1, mid);
            pstmt.setLong(2, userid);
            pstmt.executeUpdate();
            pstmt2 = conn.prepareStatement("UPDATE PRIVATE_MESSAGES P SET P.DEL_BY_RECIPIENT = 1\n" +
                    "WHERE\n" +
                    "  P.MID = ? AND P.RECIPIENT = ?");
            pstmt2.setLong(1, mid);
            pstmt2.setLong(2, userid);
            pstmt2.executeUpdate();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public void sendMessage(long sender, long recipient, String msg, long utc) throws SQLException {
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
                pstmt2 = conn.prepareStatement("INSERT INTO PRIVATE_MESSAGES(MID,RECIPIENT,DEL_BY_RECIPIENT) " +
                                "VALUES (?,?,?)");
                pstmt2.setLong(1, mid);
                pstmt2.setLong(2, recipient);
                pstmt2.setInt(3, 0); //not deleted by default
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
