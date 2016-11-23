package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.User;
import edu.ucsb.engineering.buzmo.api.UserCreationRequest;
import edu.ucsb.engineering.buzmo.api.UserSearch;
import edu.ucsb.engineering.buzmo.util.Toolbox;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDAO {

    //STATIC STRINGS FOR USER SEARCH QUERY
    private static final String S_BASE = "SELECT U.USERID, U.EMAIL, U.FULL_NAME, U.SCREENNAME, U.PHONE, " +
            "U.IS_MANAGER FROM USERS U WHERE ";
    private static final String S_EMAIL = "/* Email is similar to... */ UPPER(U.EMAIL) LIKE ?";
    private static final String S_TOPICS = "/* User has one or more matching topics. */\n" +
            "  EXISTS (\n" +
            "    /* Topics user searched for. */\n" +
            "    SELECT T.TID FROM TOPICS T WHERE T.LABEL IN (%s)\n" +
            "    INTERSECT\n" +
            "    /* Topics that belong to current user. */\n" +
            "    SELECT T.TID FROM USER_TOPICS T WHERE T.USERID = U.USERID\n" +
            "  )";
    private static final String S_RECENT = "/* Most recent posting is within last n days. */\n" +
            "  EXISTS (\n" +
            "    /* Find all posts within last n days. */\n" +
            "    SELECT M.MID FROM MESSAGES M, MC_MESSAGES S\n" +
            "    WHERE\n" +
            "      M.MID = S.MID AND\n" +
            "      M.SENDER = U.USERID AND\n" +
            "      S.IS_PUBLIC = 1 AND\n" +
            "      (? - M.MSG_TIMESTAMP) < (? * 86400000)\n" +
            "  )";
    private static final String S_NUMBER = "/* m or more number of messages posted within last 7 days */\n" +
            "  (\n" +
            "    SELECT COUNT(*) FROM MESSAGES M, MC_MESSAGES S\n" +
            "    WHERE\n" +
            "      M.MID = S.MID AND\n" +
            "      M.SENDER = U.USERID AND\n" +
            "      S.IS_PUBLIC = 1 AND\n" +
            "      (? - M.MSG_TIMESTAMP) < 604800000\n" +
            "  ) >= ?";
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
            pstmt = conn.prepareStatement("SELECT userid, email, full_name, screename, phone, is_manager " +
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
            pstmt = conn.prepareStatement("INSERT INTO USERS (USERID, EMAIL, FULL_NAME, PASSWD, PHONE, " +
                    "SCREENNAME, IS_MANAGER) " +
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

    public List<User> searchUsers(UserSearch us, int offset, int limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        List<User> toReturn = new ArrayList<>(0);
        ResultSet rs = null;
        try {
            conn = this.ds.getConnection();
            //Check if no parameters were given.
            if ((us == null) || (us.getEmail() == null && us.getM() == null && us.getN() == null &&
                    (us.getTopics() == null || us.getTopics().size() == 0))) {
                return toReturn;
            }
            //Build up SQL query based on UserSearch.
            String query = S_BASE;
            boolean isFirst = true;
            //Email
            String email = us.getEmail();
            if (email != null) {
                isFirst = false;
                query += S_EMAIL;
            }
            List<String> topics = us.getTopics();
            if (topics != null && topics.size() > 0) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    query += " AND ";
                }
                query += String.format(S_TOPICS, Toolbox.getQStr(topics.size()));
            }

            if (us.getN() != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    query += " AND ";
                }
                query += S_RECENT;
            }

            if (us.getM() != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    query += " AND ";
                }
                query += S_NUMBER;
            }

            //Prepare statement.
            pstmt = conn.prepareStatement(
                    String.format("SELECT * FROM (%s) WHERE ROWNUM > ? AND ROWNUM <= ? + ?", query));
            int paramIdx = 1;
            if (email != null) {
                pstmt.setString(paramIdx++, String.format("%%%s%%", email));
            }

            if (topics != null && topics.size() > 0) {
                for (String topic : topics) {
                    pstmt.setString(paramIdx++, topic.toLowerCase());
                }
            }

            if (us.getN() != null) {
                pstmt.setLong(paramIdx++, (new Date()).getTime());
                pstmt.setInt(paramIdx++, us.getN());
            }

            if (us.getM() != null) {
                pstmt.setLong(paramIdx++, (new Date()).getTime());
                pstmt.setInt(paramIdx++, us.getM());
            }

            pstmt.setInt(paramIdx++, offset);
            pstmt.setInt(paramIdx++, offset);
            pstmt.setInt(paramIdx++, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                toReturn.add(new User(rs.getLong("USERID"), rs.getString("FULL_NAME"), rs.getString("EMAIL"),
                        rs.getString("SCREENNAME"), rs.getLong("PHONE"), (rs.getInt("IS_MANAGER") == 1)));
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return toReturn;
    }
}
