package edu.ucsb.engineering.buzmo.api;

/* Used for posting messages to group chats or private messages */

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageInABottle {
    private String msg;
    private Long recipient;

    @JsonProperty
    public String getMsg() {
        return msg;
    }

    @JsonProperty
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonProperty
    public Long getRecipient() {
        return recipient;
    }

    @JsonProperty
    public void setRecipient(Long recipient) {
        this.recipient = recipient;
    }
}
