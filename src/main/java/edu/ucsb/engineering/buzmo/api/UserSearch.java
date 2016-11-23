package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserSearch {
    private String email;
    List<String> topics;
    //most recent posting is within last n days (n <= 7)
    private Integer n;
    //m or more messages posted within the last 7 days
    private Integer m;

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty
    public List<String> getTopics() {
        return topics;
    }

    @JsonProperty
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    @JsonProperty
    public Integer getN() {
        return n;
    }

    @JsonProperty
    public void setN(Integer n) {
        this.n = n;
    }

    @JsonProperty
    public Integer getM() {
        return m;
    }

    @JsonProperty
    public void setM(Integer m) {
        this.m = m;
    }
}
