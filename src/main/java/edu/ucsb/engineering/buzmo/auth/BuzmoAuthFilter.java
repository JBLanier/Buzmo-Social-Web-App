package edu.ucsb.engineering.buzmo.auth;

import edu.ucsb.engineering.buzmo.api.User;
import io.dropwizard.auth.AuthFilter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * https://spin.atomicobject.com/2016/07/26/dropwizard-dive-part-1/
 */
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class BuzmoAuthFilter extends AuthFilter<String, User> {

    private SessionManager sm;

    public BuzmoAuthFilter(SessionManager sm) {
        this.sm = sm;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //if credentials invalid
        MultivaluedMap<String, String> headers = requestContext.getHeaders();

        System.out.println("\n\nHeaders: " + headers.toString());

        if (headers.getOrDefault("auth_token", null) == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        String token = headers.getOrDefault("auth_token", null).get(0);
        User user = this.sm.fetchSession(token);
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        //Session is valid.
        requestContext.setSecurityContext(new BuzmoSecurityContext(requestContext.getSecurityContext(), user));
    }
}