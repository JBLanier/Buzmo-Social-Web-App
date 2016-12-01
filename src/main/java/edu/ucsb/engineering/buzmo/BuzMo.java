package edu.ucsb.engineering.buzmo;


import edu.ucsb.engineering.buzmo.config.BuzMoConfiguration;
import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;
import edu.ucsb.engineering.buzmo.daos.FriendsDAO;
import edu.ucsb.engineering.buzmo.daos.PrivateMessageDAO;
import edu.ucsb.engineering.buzmo.daos.MyCircleDAO;
import edu.ucsb.engineering.buzmo.daos.UserDAO;
import edu.ucsb.engineering.buzmo.resources.*;
import edu.ucsb.engineering.buzmo.auth.BuzmoAuthFilter;
import edu.ucsb.engineering.buzmo.time.TimeKeeperTask;
import edu.ucsb.engineering.buzmo.util.CleanupTask;
import edu.ucsb.engineering.buzmo.util.DBPoolManager;
import edu.ucsb.engineering.buzmo.auth.SessionManager;
import edu.ucsb.engineering.buzmo.time.TimeKeeper;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Environment;
import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Timer;


public class BuzMo extends Application<BuzMoConfiguration> {

    final static Logger logger = LoggerFactory.getLogger(DBPoolManager.class);

    private BasicDataSource ds = null;

    public static void main(String[] args) throws Exception {
        new BuzMo().run(args);
    }

    public void run(BuzMoConfiguration configuration, Environment environment) {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "*");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

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

        TimeKeeper tk = new TimeKeeper(ds);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimeKeeperTask(tk), 0, 10000);

        //Setup DAOs (pass them the BasicDataSource).
        UserDAO userDAO = new UserDAO(ds, tk);
        FriendsDAO friendsDAO = new FriendsDAO(ds);
        PrivateMessageDAO privateDAO = new PrivateMessageDAO(ds);
        ChatGroupsDAO chatGroupsDAO = new ChatGroupsDAO(ds);
        MyCircleDAO myCircleDAO = new MyCircleDAO(ds);

        //Session Manager
        SessionManager sm = new SessionManager();

        //Chat Group Message Cleanup Task
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new CleanupTask(tk, chatGroupsDAO), 0, 10000); //10 second interval

        //Register resources.
        environment.jersey().register(new HelloResource());
        environment.jersey().register(new FriendsResource(friendsDAO, userDAO, tk));
        environment.jersey().register(new UserResource(userDAO));
        environment.jersey().register(new AuthResource(sm, userDAO));
        environment.jersey().register(new PrivateMessagesResource(privateDAO, tk));
        environment.jersey().register(new ChatGroupsResource(chatGroupsDAO, tk));
        environment.jersey().register(new MyCircleResource(myCircleDAO, userDAO, tk));
        environment.jersey().register(new TimeResource(tk, chatGroupsDAO));

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
