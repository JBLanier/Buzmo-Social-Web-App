package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * For use in group chats and private message conversations.
 */

public class ConversationMessage {
    //sender
    private String screenname;
    private long userid;

    private String msg; //message content
    private long utc; //timestamp

    public ConversationMessage() {
    }

    public ConversationMessage(String screenname, long userid, String msg, long utc) {
        this.screenname = screenname;
        this.userid = userid;
        this.msg = msg;
        this.utc = utc;
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
}
