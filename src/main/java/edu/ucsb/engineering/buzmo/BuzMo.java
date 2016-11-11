package edu.ucsb.engineering.buzmo;

import edu.ucsb.engineering.buzmo.config.BuzMoConfiguration;
import edu.ucsb.engineering.buzmo.daos.UserDAO;
import edu.ucsb.engineering.buzmo.resources.HelloResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.dbcp2.BasicDataSource;

public class BuzMo extends Application<BuzMoConfiguration> {
    public static void main(String[] args) throws Exception {
        new BuzMo().run(args);
    }

    @Override
    public void initialize(Bootstrap<BuzMoConfiguration> bootstrap) {
        //Serve up static resources for the frontend.
        //Serves everything in the src/main/resources/frontend folder.
        bootstrap.addBundle(new AssetsBundle("/frontend", "/", "index.html"));
    }

    public void run(BuzMoConfiguration configuration, Environment environment) {
        //Setup DB connection pool (BasicDataSource).
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setUrl(configuration.getDbConfig().getEndpoint());
        ds.setUsername(configuration.getDbConfig().getUsername());
        ds.setPassword(configuration.getDbConfig().getPassword());
        //Validation Query and Test on Borrow
        //More needed in a real world system where connections will get stale (timeout) and disconnect
        //after a while.
        //Query to see if DB connection is still alive.
        //http://vondrnotes.blogspot.com/2012/05/validationquery-for-different-databases.html
        ds.setValidationQuery("SELECT 1 FROM DUAL");
        //Set validation query timeout to 15 seconds.
        ds.setValidationQueryTimeout(15);
        ds.setTestOnBorrow(true);
        //Our BasicDataSource ds is ready to go!

        //Setup DAOs (pass them the BasicDataSource).
        UserDAO userDAO = new UserDAO(ds);

        //Register resources.
        environment.jersey().register(new HelloResource());

        //We could now pass in userDAO to a resource via that resource's constructor.
        //That resource could then store userDAO in a field.
        //Then when a request comes in, the method that handles the request in the resource
        //could make a call to a method of userDAO.

    }
}
