package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.MyCircleMessage;
import edu.ucsb.engineering.buzmo.util.Toolbox;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jb on 11/13/16.
 */
public class MyCircleDAO {
    private BasicDataSource ds;

    public MyCircleDAO(BasicDataSource ds) {
        this.ds = ds;
    }

    public List<MyCircleMessage> getMessages(long userid, long offset, long limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<MyCircleMessage> messages = new ArrayList<>();
        Map<Long,List<String>> topicMap = new HashMap<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement("/* MID, SCREENNAME, USERID, UTC, MSG, IS_PUBLIC, IS_BROADCAST, READ_COUNT */\n" +
                    "/* Get my circle messages sent directly to me. */\n" +
                    "SELECT * FROM (\n" +
                    "  SELECT *\n" +
                    "  FROM (\n" +
                    "    SELECT\n" +
                    "      M.MID,\n" +
                    "      U.SCREENNAME,\n" +
                    "      U.USERID,\n" +
                    "      M.MSG_TIMESTAMP AS UTC,\n" +
                    "      M.MSG,\n" +
                    "      S.IS_PUBLIC,\n" +
                    "      S.IS_BROADCAST,\n" +
                    "      S.READ_COUNT\n" +
                    "    FROM MESSAGES M, MC_MESSAGES S, MC_MSG_RECIPIENTS R, USERS U\n" +
                    "    WHERE\n" +
                    "      M.MID = S.MID AND\n" +
                    "      S.MID = R.MID AND\n" +
                    "      U.USERID = M.SENDER AND\n" +
                    "      M.IS_DELETED = 0 AND\n" +
                    "      S.IS_PUBLIC = 0 AND /* should always be private if sent directly, this is just for safety */\n" +
                    "      /* message sent to me or from me */\n" +
                    "      (R.RECIPIENT = ? OR M.SENDER = ?)\n" +
                    "    UNION\n" +
                    "    /* Get my circle messages broadcasted by my friends. */\n" +
                    "    SELECT\n" +
                    "      M.MID,\n" +
                    "      U.SCREENNAME,\n" +
                    "      U.USERID,\n" +
                    "      M.MSG_TIMESTAMP AS UTC,\n" +
                    "      M.MSG,\n" +
                    "      S.IS_PUBLIC,\n" +
                    "      S.IS_BROADCAST,\n" +
                    "      S.READ_COUNT\n" +
                    "    FROM MESSAGES M, MC_MESSAGES S, USERS U\n" +
                    "    WHERE\n" +
                    "      M.MID = S.MID AND\n" +
                    "      M.IS_DELETED = 0 AND\n" +
                    "      U.USERID = M.SENDER AND\n" +
                    "      S.IS_PUBLIC = 1 AND\n" +
                    "      (\n" +
                    "        /* message sent by my friends */\n" +
                    "        M.SENDER IN (\n" +
                    "          /* Get all userIds of my friends. */\n" +
                    "          SELECT F.U1\n" +
                    "          FROM FRIENDS F\n" +
                    "          WHERE F.U2 = ?\n" +
                    "          UNION\n" +
                    "          SELECT F.U2\n" +
                    "          FROM FRIENDS F\n" +
                    "          WHERE F.U1 = ?\n" +
                    "        )\n" +
                    "        OR\n" +
                    "        /* message sent by me */\n" +
                    "        M.SENDER = ?\n" +
                    "      )\n" +
                    "  )\n" +
                    "  ORDER BY UTC DESC\n" +
                    ")\n" +
                    "WHERE\n" +
                    "    ROWNUM > ? AND\n" +
                    "    ROWNUM <= ? + ?");
            pstmt.setLong(1, userid);
            pstmt.setLong(2, userid);
            pstmt.setLong(3, userid);
            pstmt.setLong(4, userid);
            pstmt.setLong(5, userid);
            pstmt.setLong(6, offset);
            pstmt.setLong(7, offset);
            pstmt.setLong(8, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                //String screenname, long userid, String msg, long utc, long mid, List<String> topics,
                           //boolean isPublic, boolean isBroadcast, long readCount
                MyCircleMessage msg = new MyCircleMessage(rs.getString("SCREENNAME"), rs.getLong("USERID"), rs.getString("MSG"),
                        rs.getLong("UTC"), rs.getLong("MID"), new ArrayList<String>(), (rs.getInt("IS_PUBLIC") == 1),
                        (rs.getInt("IS_BROADCAST") == 1), rs.getLong("READ_COUNT"));
                messages.add(msg);
                topicMap.put(msg.getMid(), msg.getTopics());
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        //Fetch topics for messages.
        this.loadTopics(messages);
        return messages;
    }

    //Assumes that lists have already been initialized (not null).
    private void loadTopics(List<MyCircleMessage> messages) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        Map<Long, List<String>> topicMap = new HashMap<>();
        try {
            conn = this.ds.getConnection();
            //Build SQL.
            String set = "";
            for (int i = 0; i < messages.size(); i++) {
                set += Long.toString(messages.get(i).getMid());
                topicMap.put(messages.get(i).getMid(), messages.get(i).getTopics());
                if (i != messages.size() - 1) {
                    set += ",";
                }
            }
            stmt = conn.createStatement();
            rs = stmt.executeQuery(String.format("SELECT DISTINCT M.MID, T.LABEL\n" +
                    "FROM MC_MSG_TOPICS M, TOPICS T\n" +
                    "WHERE\n" +
                    "  M.TID = T.TID AND\n" +
                    "  M.MID IN (%s)", set));

            while (rs.next()) {
                topicMap.get(rs.getLong("MID")).add(rs.getString("LABEL"));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public List<MyCircleMessage> searchAllTopics(List<String> topics, long limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<MyCircleMessage> messages = new ArrayList<>();
        Map<Long,List<String>> topicMap = new HashMap<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement(String.format("/* Search public messages by topic. */\n" +
                    "SELECT * FROM (\n" +
                    "  /* Get all public messages that match all topic words. */\n" +
                    "  SELECT\n" +
                    "    M.MID,\n" +
                    "    U.SCREENNAME,\n" +
                    "    U.USERID,\n" +
                    "    M.MSG_TIMESTAMP AS UTC,\n" +
                    "    M.MSG,\n" +
                    "    S.IS_PUBLIC,\n" +
                    "    S.IS_BROADCAST,\n" +
                    "    S.READ_COUNT\n" +
                    "  FROM MESSAGES M, MC_MESSAGES S, USERS U\n" +
                    "  WHERE\n" +
                    "    M.MID = S.MID AND\n" +
                    "    M.IS_DELETED = 0 AND\n" +
                    "    U.USERID = M.SENDER AND\n" +
                    "    S.IS_PUBLIC = 1 AND\n" +
                    "    NOT EXISTS (\n" +
                    "      /* Topics we are searching for. */\n" +
                    "      SELECT T.TID FROM TOPICS T WHERE T.LABEL IN (%s)\n" +
                    "      MINUS\n" +
                    "      /* Topics for a given message. */\n" +
                    "      SELECT T.TID FROM TOPICS T, MC_MSG_TOPICS P WHERE T.TID = P.TID AND P.MID = M.MID\n" +
                    "    )\n" +
                    "  ORDER BY UTC DESC\n" +
                    ") WHERE\n" +
                    "    ROWNUM <= ?", Toolbox.getQStr(topics.size())));
            pstmt.setLong(1, limit);
            for (int i = 0; i < topics.size(); i++) {
                pstmt.setString(i + 2, topics.get(i));
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                //String screenname, long userid, String msg, long utc, long mid, List<String> topics,
                //boolean isPublic, boolean isBroadcast, long readCount
                MyCircleMessage msg = new MyCircleMessage(rs.getString("SCREENNAME"), rs.getLong("USERID"), rs.getString("MSG"),
                        rs.getLong("UTC"), rs.getLong("MID"), new ArrayList<String>(), (rs.getInt("IS_PUBLIC") == 1),
                        (rs.getInt("IS_BROADCAST") == 1), rs.getLong("READ_COUNT"));
                messages.add(msg);
                topicMap.put(msg.getMid(), msg.getTopics());
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        //Fetch topics for messages.
        this.loadTopics(messages);
        return messages;
    }

    public List<MyCircleMessage> searchAtLeastTopics(List<String> topics, long limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<MyCircleMessage> messages = new ArrayList<>();
        Map<Long,List<String>> topicMap = new HashMap<>();
        try {
            conn = this.ds.getConnection();
            pstmt = conn.prepareStatement(String.format("/* Search public messages by topic. */\n" +
                    "SELECT * FROM (\n" +
                    "  /* Get all public messages that match at least one topic word. */\n" +
                    "  SELECT\n" +
                    "    M.MID,\n" +
                    "    U.SCREENNAME,\n" +
                    "    U.USERID,\n" +
                    "    M.MSG_TIMESTAMP AS UTC,\n" +
                    "    M.MSG,\n" +
                    "    S.IS_PUBLIC,\n" +
                    "    S.IS_BROADCAST,\n" +
                    "    S.READ_COUNT\n" +
                    "  FROM MESSAGES M, MC_MESSAGES S, USERS U\n" +
                    "  WHERE\n" +
                    "    M.MID = S.MID AND\n" +
                    "    M.IS_DELETED = 0 AND\n" +
                    "    U.USERID = M.SENDER AND\n" +
                    "    S.IS_PUBLIC = 1 AND\n" +
                    "    EXISTS (\n" +
                    "      /* all topics searched for */\n" +
                    "      SELECT D1.TID FROM TOPICS D1 WHERE D1.LABEL IN (%s)\n" +
                    "      INTERSECT\n" +
                    "      /* all topics for message */\n" +
                    "      SELECT D2.TID FROM MC_MSG_TOPICS D2 WHERE D2.MID = M.MID\n" +
                    "    )\n" +
                    "  ORDER BY UTC DESC\n" +
                    ") WHERE\n" +
                    "    ROWNUM <= ?", Toolbox.getQStr(topics.size())));
            pstmt.setLong(1, limit);
            for (int i = 0; i < topics.size(); i++) {
                pstmt.setString(i + 2, topics.get(i));
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                //String screenname, long userid, String msg, long utc, long mid, List<String> topics,
                //boolean isPublic, boolean isBroadcast, long readCount
                MyCircleMessage msg = new MyCircleMessage(rs.getString("SCREENNAME"), rs.getLong("USERID"), rs.getString("MSG"),
                        rs.getLong("UTC"), rs.getLong("MID"), new ArrayList<String>(), (rs.getInt("IS_PUBLIC") == 1),
                        (rs.getInt("IS_BROADCAST") == 1), rs.getLong("READ_COUNT"));
                messages.add(msg);
                topicMap.put(msg.getMid(), msg.getTopics());
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        //Fetch topics for messages.
        this.loadTopics(messages);
        return messages;
    }

}
