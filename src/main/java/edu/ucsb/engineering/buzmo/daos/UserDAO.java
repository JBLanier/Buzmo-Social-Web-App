package edu.ucsb.engineering.buzmo.daos;

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

    public void getUser(long uid) throws SQLException {
        Connection conn = this.ds.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT email, name, screename, phone, is_manager FROM users WHERE uid = ?");
        pstmt.setLong(1, uid);
        ResultSet rs = pstmt.executeQuery();


        conn.close();
    }
}
