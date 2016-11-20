package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used in list for group chats and private messages.
 */
public class ConversationListItem {
    private String name; //group chat name or friend name
    private long uniqueId; //gcid or userid
    private long utc; //timestamp of last message in conversation

    @JsonCreator
    public ConversationListItem(String name, long uniqueId, long utc) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.utc = utc;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public long getUniqueId() {
        return uniqueId;
    }

    @JsonProperty
    public long getUtc() {
        return utc;
    }
}
