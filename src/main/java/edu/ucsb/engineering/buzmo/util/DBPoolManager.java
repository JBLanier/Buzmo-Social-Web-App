package edu.ucsb.engineering.buzmo.util;

import io.dropwizard.lifecycle.Managed;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * To properly shutdown DB pool when the application terminates.
 */
public class DBPoolManager implements Managed {

    private BasicDataSource ds;

    final static Logger logger = LoggerFactory.getLogger(DBPoolManager.class);

    public DBPoolManager(BasicDataSource ds) {
        this.ds = ds;
    }

    @Override
    public void start() throws Exception {
        //nothing to do
    }

    @Override
    public void stop() throws Exception {
        logger.info("Shutting down connection pool...");
        try {
            this.ds.close();
        } catch (SQLException e) {
            //nothing we can do
            logger.warn("Error shuttdown down connection pool.", e);
        }
    }
}
