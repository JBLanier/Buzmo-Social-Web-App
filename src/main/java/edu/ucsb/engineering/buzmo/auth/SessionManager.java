package edu.ucsb.engineering.buzmo.auth;

import edu.ucsb.engineering.buzmo.api.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private Map<String,User> store;

    public SessionManager() {
        this.store = new HashMap<>();
    }

    public String startSession(User user) {
        String uuid = UUID.randomUUID().toString();
        this.store.put(uuid, user);
        return uuid;
    }

    public User fetchSession(String token) {
        return this.store.get(token);
    }

    //TODO: Have sessions expire.
}

