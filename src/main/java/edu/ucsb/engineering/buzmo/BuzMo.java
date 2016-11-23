package edu.ucsb.engineering.buzmo;


import edu.ucsb.engineering.buzmo.config.BuzMoConfiguration;
import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;
import edu.ucsb.engineering.buzmo.daos.FriendsDAO;
import edu.ucsb.engineering.buzmo.daos.PrivateMessageDAO;
import edu.ucsb.engineering.buzmo.daos.MyCircleDAO;
import edu.ucsb.engineering.buzmo.daos.UserDAO;
import edu.ucsb.engineering.buzmo.resources.*;
import edu.ucsb.engineering.buzmo.auth.BuzmoAuthFilter;
import edu.ucsb.engineering.buzmo.util.DBPoolManager;
import edu.ucsb.engineering.buzmo.auth.SessionManager;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.dbcp2.BasicDataSource;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BuzMo extends Application<BuzMoConfiguration> {

    final static Logger logger = LoggerFactory.getLogger(DBPoolManager.class);

    private BasicDataSource ds = null;

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

        logger.info("Configuring connection pool...");
        //Setup DB connection pool (BasicDataSource).
        ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setUrl(configuration.getDbConfig().getEndpoint());
        ds.setUsername(configuration.getDbConfig().getUsername());
        ds.setPassword(configuration.getDbConfig().getPassword());
        //To not strain the class DB.
        ds.setMaxIdle(2);
        ds.setMaxTotal(2);
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
        FriendsDAO friendsDAO = new FriendsDAO(ds);
        PrivateMessageDAO privateDAO = new PrivateMessageDAO(ds);
        ChatGroupsDAO chatGroupsDAO = new ChatGroupsDAO(ds);
        MyCircleDAO myCircleDAO = new MyCircleDAO(ds);

        //Session Manager
        SessionManager sm = new SessionManager();

        //Register resources.
        environment.jersey().register(new HelloResource());
        environment.jersey().register(new FriendsResource(friendsDAO));
        environment.jersey().register(new UserResource(userDAO));
        environment.jersey().register(new AuthResource(sm, userDAO));
        environment.jersey().register(new PrivateMessagesResource(privateDAO));
        environment.jersey().register(new ChatGroupsResource(chatGroupsDAO));
        environment.jersey().register(new MyCircleResource(myCircleDAO));

        //We could now pass in userDAO to a resource via that resource's constructor.
        //That resource could then store userDAO in a field.
        //Then when a request comes in, the method that handles the request in the resource
        //could make a call to a method of userDAO.

        //Register Lifecycle Managers
        DBPoolManager poolMan = new DBPoolManager(this.ds);
        environment.lifecycle().manage(poolMan);

        environment.jersey().register(new AuthDynamicFeature(new BuzmoAuthFilter(sm)));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }
}
