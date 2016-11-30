package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGroupInviteResponse {

    private long cgid;
    private String groupName;
    private String invitedBy;
    private long utc;

    public ChatGroupInviteResponse() {
    }

    public ChatGroupInviteResponse(long cgid, String groupName, String invitedBy, long utc) {
        this.cgid = cgid;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.utc = utc;
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
    public String getGroupName() {
        return groupName;
    }

    @JsonProperty
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @JsonProperty
    public String getInvitedBy() {
        return invitedBy;
    }

    @JsonProperty
    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
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
