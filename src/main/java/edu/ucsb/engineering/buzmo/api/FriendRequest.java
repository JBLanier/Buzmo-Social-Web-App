package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FriendRequest {
    private long mid;
    private long recipient;
    private String msg;
    private long msg_timestamp;
    private long sender;
    private String sender_name;

    @JsonCreator
    public FriendRequest(@JsonProperty("mid") long mid, @JsonProperty("recipient") long recipient,
                @JsonProperty("msg") String msg, @JsonProperty("msg_timestamp") long msg_timestamp,
                @JsonProperty("sender") long sender, @JsonProperty("sender_name") String sender_name) {
        this.mid = mid;
        this.recipient = recipient;
        this.msg = msg;
        this.msg_timestamp = msg_timestamp;
        this.sender = sender;
        this.sender_name = sender_name;
    }

    @JsonProperty
    public long getMid() {
        return mid;
    }

    @JsonProperty
    public long getRecipient() {
        return recipient;
    }

    @JsonProperty
    public String getMsg() {
        return msg;
    }

    @JsonProperty
    public long getMsg_timestamp() {
        return msg_timestamp;
    }

    @JsonProperty
    public long getSender() {
        return sender;
    }

    @JsonProperty
    public String getSender_name() {
        return sender_name;
    }
}
