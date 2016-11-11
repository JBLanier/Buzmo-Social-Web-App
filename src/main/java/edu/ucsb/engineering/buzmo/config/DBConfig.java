package edu.ucsb.engineering.buzmo.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBConfig {
    private String endpoint;
    private String username;
    private String password;

    @JsonProperty
    public String getEndpoint() {
        return endpoint;
    }

    @JsonProperty
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
