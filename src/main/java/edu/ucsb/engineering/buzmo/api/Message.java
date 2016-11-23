package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * For use in group chats, private message conversations, and my circle messages.
 *
 * This is the object to be returned to the UI.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    //sender
    private String screenname;
    private long userid;

    private String msg; //message content
    private long mid;
    private long utc; //timestamp

    //only used by my circle messages
    private List<String> topics;

    public Message() {
    }

    public Message(String screenname, long userid, String msg, long utc, long mid) {
        this.screenname = screenname;
        this.userid = userid;
        this.msg = msg;
        this.utc = utc;
        this.mid = mid;
    }

    public Message(String screenname, long userid, String msg, long utc, long mid, List<String> topics) {
        this.screenname = screenname;
        this.userid = userid;
        this.msg = msg;
        this.utc = utc;
        this.mid = mid;
        this.topics = topics;
    }

    @JsonProperty
    public String getScreenname() {
        return screenname;
    }

    @JsonProperty
    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    @JsonProperty
    public long getUserid() {
        return userid;
    }

    @JsonProperty
    public void setUserid(long userid) {
        this.userid = userid;
    }

    @JsonProperty
    public String getMsg() {
        return msg;
    }

    @JsonProperty
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonProperty
    public long getUtc() {
        return utc;
    }

    @JsonProperty
    public void setUtc(long utc) {
        this.utc = utc;
    }

    @JsonProperty
    public long getMid() {
        return mid;
    }

    @JsonProperty
    public void setMid(long mid) {
        this.mid = mid;
    }

    @JsonProperty
    public List<String> getTopics() {
        return topics;
    }

    @JsonProperty
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
