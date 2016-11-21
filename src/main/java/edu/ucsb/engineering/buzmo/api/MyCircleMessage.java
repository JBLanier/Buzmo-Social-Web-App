package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MyCircleMessage extends Message{
    private List<String> topics;
    private boolean isPublic;
    private boolean isBroadcast;
    private long readCount;

    public MyCircleMessage(String screenname, long userid, String msg, long utc, long mid, List<String> topics,
                           boolean isPublic, boolean isBroadcast, long readCount) {
        super(screenname, userid, msg, utc, mid);
        this.topics = topics;
        this.isPublic = isPublic;
        this.isBroadcast = isBroadcast;
        this.readCount = readCount;
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
    public boolean isPublic() {
        return isPublic;
    }

    @JsonProperty
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @JsonProperty
    public boolean isBroadcast() {
        return isBroadcast;
    }

    @JsonProperty
    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }

    @JsonProperty
    public long getReadCount() {
        return readCount;
    }

    @JsonProperty
    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }
}
