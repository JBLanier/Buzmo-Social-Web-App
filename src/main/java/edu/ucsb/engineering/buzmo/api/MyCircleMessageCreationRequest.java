package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MyCircleMessageCreationRequest{

    private List<String> recipients;
    private Long userid;
    private Long utc;
    private List<String> topics;
    private Boolean isPublic;
    private String msg;

    public MyCircleMessageCreationRequest() {
    }

    public MyCircleMessageCreationRequest(List<String> recipients, long userid, long utc, List<String> topics, boolean isPublic, String msg) {
        this.recipients = recipients;
        this.userid = userid;
        this.utc = utc;
        this.topics = topics;
        this.isPublic = isPublic;
        this.msg = msg;
    }

    @JsonProperty
    public List<String> getRecipients() {
        return recipients;
    }

    @JsonProperty
    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    @JsonProperty
    public Long getUserid() {
        return userid;
    }

    @JsonProperty
    public void setUserid(Long userid) {
        this.userid = userid;
    }

    @JsonProperty
    public Long getUtc() {
        return utc;
    }

    @JsonProperty
    public void setUtc(Long utc) {
        this.utc = utc;
    }

    @JsonProperty
    public List<String> getTopics() {
        return topics;
    }

    @JsonProperty
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    @JsonProperty("public")
    public boolean isPublic() {
        return isPublic;
    }

    @JsonProperty("public")
    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    @JsonProperty
    public String getMsg() {
        return msg;
    }

    @JsonProperty
    public void setMsg(String msg) {
        this.msg = msg;
    }
    @JsonIgnore
    public boolean isBroadcast() {
        return (this.recipients == null || this.recipients.size() == 0);
    }
}
