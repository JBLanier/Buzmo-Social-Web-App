package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatGroup {
    private long cgid;
    private String name;
    private long duration;
    private long owner;

    public ChatGroup() {
    }

    public ChatGroup(long cgid, String name, long duration, long owner) {
        this.cgid = cgid;
        this.name = name;
        this.duration = duration;
        this.owner = owner;
    }

    @JsonProperty
    public long getCgid() {
        return cgid;
    }

    @JsonProperty
    public void setCgid(long cgid) {
        this.cgid = cgid;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public long getDuration() {
        return duration;
    }

    @JsonProperty
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @JsonProperty
    public long getOwner() {
        return owner;
    }

    @JsonProperty
    public void setOwner(long owner) {
        this.owner = owner;
    }
}
