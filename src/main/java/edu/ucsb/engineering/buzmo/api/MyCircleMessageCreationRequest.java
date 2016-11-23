package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MyCircleMessageCreationRequest extends MyCircleMessage {

    private List<Long> recipients;

    public MyCircleMessageCreationRequest(String screenname, long userid, String msg, long utc, long mid, List<String> topics,
                           boolean isPublic, boolean isBroadcast, long readCount, List<Long> recipients) {
        super(screenname,userid,msg,utc,mid,topics,isPublic,isBroadcast,readCount);
        this.recipients = recipients;
    }

    @JsonProperty
    public List<Long> getRecipients() {
        return recipients;
    }
}
