package edu.ucsb.engineering.buzmo.daos;

import edu.ucsb.engineering.buzmo.api.FriendRequest;
import edu.ucsb.engineering.buzmo.api.Report;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    private BasicDataSource ds;

    public ReportDAO(BasicDataSource ds) {
        this.ds = ds;
    }

    public Report getReport(long since, long now) throws SQLException {

        Connection conn = null;

        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;
        PreparedStatement pstmt5 = null;
        PreparedStatement pstmt6 = null;
        PreparedStatement pstmt7 = null;
        PreparedStatement pstmt8 = null;

        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        ResultSet rs5 = null;
        ResultSet rs6 = null;
        ResultSet rs7 = null;
        ResultSet rs8 = null;

        Report toReturn = null;


        try {
            conn = this.ds.getConnection();


            pstmt1 = conn.prepareStatement(
                    "SELECT COUNT(*) AS COUNT\n" +
                    "FROM MESSAGES M\n" +
                    "WHERE M.MSG_TIMESTAMP > ?");
            pstmt1.setLong(1, since);
            rs1 = pstmt1.executeQuery();

            pstmt2 = conn.prepareStatement(
                    "SELECT COUNT(*) AS COUNT\n" +
                            "FROM MC_READS M\n" +
                            "WHERE M.UTC > ?");
            pstmt2.setLong(1, since);
            rs2 = pstmt2.executeQuery();

            pstmt3 = conn.prepareStatement(
                    "SELECT AVG(READ_COUNT) AS AVG\n" +
                            "FROM (SELECT COUNT(*) AS READ_COUNT\n" +
                            "      FROM MC_READS MR\n" +
                            "      WHERE MR.UTC > ?\n" +
                            "      GROUP BY MR.MID)");
            pstmt3.setLong(1, since);
            rs3 = pstmt3.executeQuery();

            pstmt4 = conn.prepareStatement(
                    "SELECT AVG(READ_COUNT) AS AVG\n" +
                            "FROM (SELECT COUNT(*) AS READ_COUNT\n" +
                            "      FROM MC_READS MR, MESSAGES M\n" +
                            "      WHERE M.MSG_TIMESTAMP > ? AND " +
                            "       MR.MID = M.MID\n" +
                            "      GROUP BY MR.MID)");
            pstmt4.setLong(1, since);
            rs4 = pstmt4.executeQuery();

            pstmt5 = conn.prepareStatement(
                    "SELECT * FROM\n" +
                            "  (SELECT M.MID, M.MSG, M.SENDER, COUNT(R.UTC) AS READ_COUNT\n" +
                            "   FROM MC_MESSAGES MC, MESSAGES M, MC_READS R, USERS U\n" +
                            "   WHERE M.MID = R.MID AND\n" +
                            "         MC.MID = M.MID AND\n" +
                            "         R.UTC > ? AND\n" +
                            "         U.USERID = M.SENDER\n" +
                            "   GROUP BY M.MID, M.MSG, M.SENDER\n" +
                            "   ORDER BY READ_COUNT DESC)\n" +
                            "WHERE rownum <= 3");
            pstmt5.setLong(1, since);
            rs5 = pstmt5.executeQuery();

            pstmt6 = conn.prepareStatement(
                    "SELECT * FROM\n" +
                            "  (SELECT U.USERID, U.FULL_NAME, U.SCREENNAME, U.EMAIL, COUNT(M.MID) AS MESSAGE_COUNT\n" +
                            "   FROM USERS U, MESSAGES M\n" +
                            "   WHERE M.SENDER = U.USERID AND\n" +
                            "         M.MSG_TIMESTAMP > ? \n" +
                            "   GROUP BY U.USERID, U.FULL_NAME, U.SCREENNAME, U.EMAIL\n" +
                            "   ORDER BY MESSAGE_COUNT DESC)\n" +
                            "WHERE rownum <= 3");
            pstmt6.setLong(1, since);
            rs6 = pstmt6.executeQuery();

            pstmt7 = conn.prepareStatement(
                    "SELECT DISTINCT * FROM\n" +
                            "  ((SELECT U.USERID, U.FULL_NAME, U.SCREENNAME, U.EMAIL, COUNT(M.MID) AS MESSAGE_COUNT\n" +
                            "    FROM USERS U, MESSAGES M\n" +
                            "    WHERE (M.SENDER = U.USERID AND\n" +
                            "           M.MSG_TIMESTAMP > ?)\n" +
                            "    GROUP BY U.USERID, U.FULL_NAME, U.SCREENNAME, U.EMAIL)\n" +
                            "   UNION\n" +
                            "   (SELECT U2.USERID, U2.FULL_NAME, U2.SCREENNAME, U2.EMAIL, 0 AS MESSAGE_COUNT\n" +
                            "    FROM USERS U2\n" +
                            "    WHERE NOT EXISTS(SELECT * FROM MESSAGES M2\n" +
                            "    WHERE U2.USERID = M2.SENDER AND\n" +
                            "          M2.MSG_TIMESTAMP > ?)))\n" +
                            "WHERE MESSAGE_COUNT < 3");
            pstmt7.setLong(1, since);
            pstmt7.setLong(2, since);
            rs7 = pstmt7.executeQuery();

            pstmt8 = conn.prepareStatement(
                    "SELECT T.TID, T.LABEL, E.MID, B.READ_COUNT, E.MSG, U.SCREENNAME FROM\n" +
                            "  (SELECT MT.TID, MAX(READ_COUNT) AS MAX_READ_COUNT\n" +
                            "   FROM\n" +
                            "     (SELECT M.MID, COUNT(MR.UTC) AS READ_COUNT\n" +
                            "      FROM MC_MESSAGES M, MC_READS MR\n" +
                            "      WHERE MR.MID = M.MID\n" +
                            "      GROUP BY M.MID) R, MC_MSG_TOPICS MT\n" +
                            "   WHERE MT.MID = R.MID\n" +
                            "   GROUP BY MT.TID) A,\n" +
                            "  (SELECT M2.MID, COUNT(MR2.UTC) AS READ_COUNT\n" +
                            "   FROM MC_MESSAGES M2, MC_READS MR2\n" +
                            "   WHERE MR2.MID = M2.MID\n" +
                            "   GROUP BY M2.MID) B,\n" +
                            "  TOPICS T,\n" +
                            "  MESSAGES E,\n" +
                            "  Users U\n" +
                            "WHERE B.READ_COUNT = A.MAX_READ_COUNT AND\n" +
                            "      T.TID=A.TID AND\n" +
                            "      B.MID = E.MID AND\n" +
                            "      U.USERID = E.SENDER\n" +
                            "ORDER BY TID");
            rs8 = pstmt8.executeQuery();


            //Get the first result, if one is found.
            if (rs1.next() && rs2.next() && rs3.next() && rs4.next()) {

                long numNewMessages = rs1.getLong("COUNT");

                long numMessageReads = rs2.getLong("COUNT");

                long avgReads = rs3.getLong("AVG");

                long avgNewMessageReads = rs4.getLong("AVG");

                List<String> top3Messages = new ArrayList<>();
                while (rs5.next()) {
                    //M.MID, M.MSG, M.SENDER, COUNT(R.UTC) AS READ_COUNT
                    top3Messages.add("READ COUNT: " + rs5.getLong("READ_COUNT") + " Mid: " + rs5.getLong("MID") +
                            " Sender: " + rs5.getLong("SENDER") + " " + rs5.getString("MSG"));
                }

                List<String> top3Users = new ArrayList<>();
                while (rs6.next()) {
                    //SELECT U.USERID, U.FULL_NAME, U.SCREENNAME, U.EMAIL, COUNT(M.MID) AS MESSAGE_COUNT
                    top3Users.add("Message Count: " + rs6.getLong("MESSAGE_COUNT") + " User ID: " +
                            rs6.getLong("USERID") + " Full Name: " + rs6.getString("FULL_NAME") +
                    " Screen Name: " + rs6.getString("SCREENNAME") + " EMAIL: " + rs6.getString("EMAIL"));
                }

                List<String> lowActivityUsers =  new ArrayList<>();
                while (rs7.next()) {
                    //(SELECT U.USERID, U.FULL_NAME, U.SCREENNAME, U.EMAIL, COUNT(M.MID) AS MESSAGE_COUNT
                    lowActivityUsers.add("User ID: " + rs7.getLong("USERID") + " Full Name: " + rs7.getString("FULL_NAME") +
                    " Screen Name: " + rs7.getString("SCREENNAME") + " Email: " + rs7.getString("EMAIL") + " Message Count: " +
                    rs7.getLong("MESSAGE_COUNT"));
                }

                List<String> topMessagesForTopics =  new ArrayList<>();
                while (rs8.next()) {
                    //SELECT T.TID, T.LABEL, E.MID, B.READ_COUNT, E.MSG, U.SCREENNAME
                    topMessagesForTopics.add("Topic ID: " + rs8.getLong("TID") + " Topic: " + rs8.getString("LABEL") +
                    " MID: " + rs8.getLong("MID") + " Read Count: " + rs8.getLong("READ_COUNT") + " Message: " +
                    rs8.getString("MSG") + " Screen Name: " + rs8.getString("SCREENNAME"));

                }


                toReturn = new Report(since, now, numNewMessages, numMessageReads, avgReads, avgNewMessageReads,
                        top3Messages,top3Users,lowActivityUsers,topMessagesForTopics);


            } else {
                System.out.print("\n\nOne of the report queries didn't return a result set\n\n");
            }


        } finally {
            try { if (rs1 != null) rs1.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (rs2 != null) rs2.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (rs3 != null) rs3.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (rs4 != null) rs4.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (rs5 != null) rs5.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (rs6 != null) rs6.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (rs7 != null) rs7.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (rs8 != null) rs8.close(); } catch (Exception e) {System.out.println(e.getMessage());}

            try { if (pstmt1 != null) pstmt1.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt2 != null) pstmt2.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt3 != null) pstmt3.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt4 != null) pstmt4.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt5 != null) pstmt5.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt6 != null) pstmt6.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt7 != null) pstmt7.close(); } catch (Exception e) {System.out.println(e.getMessage());}
            try { if (pstmt8 != null) pstmt8.close(); } catch (Exception e) {System.out.println(e.getMessage());}

            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());}
        }

        return toReturn;
    }


}

