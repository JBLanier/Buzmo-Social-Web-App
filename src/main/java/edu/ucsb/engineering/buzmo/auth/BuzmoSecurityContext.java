package edu.ucsb.engineering.buzmo.auth;

import edu.ucsb.engineering.buzmo.api.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class BuzmoSecurityContext implements SecurityContext {

    private SecurityContext sc;

    private User principal;

    public BuzmoSecurityContext(SecurityContext sc, User principal) {
        this.sc = sc;
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String s) {
        return this.principal.isManager() && s.equals("MANAGER");
    }

    @Override
    public boolean isSecure() {
        return this.sc.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return "AUTH_TOKEN";
    }
}
