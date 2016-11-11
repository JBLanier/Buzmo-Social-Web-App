package edu.ucsb.engineering.buzmo.daos;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class UserDAO {
    private DataSource ds;

    public UserDAO(DataSource ds) {
        this.ds = ds;
    }

    public void doSomething() throws SQLException {
        Connection conn = this.ds.getConnection();
        //do stuff with connection
        conn.close();
    }
}
