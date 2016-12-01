package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jb on 11/30/16.
 */
public class Report {

    long startUTC;
    long endUTC;


    private long numNewMessages;
    private long numMessageReads;
    private long avgReads;
    private long avgNewMessageReads;
    private List<String> top3Messages;
    private List<String> top3Users;
    private List<String> lowActivityUsers;
    private List<String> topMessagesForTopics;

    @JsonCreator
    public Report(@JsonProperty("startUTC") long startUTC,
                @JsonProperty("endUTC") long endUTC,
                @JsonProperty("numNewMessages") long numNewMessages,
                @JsonProperty("numMessageReads") long numMessageReads,
                @JsonProperty("avgReads") long avgReads,
                @JsonProperty("avgNewMessageReads") long avgNewMessageReads,
                @JsonProperty("top3Messages") List<String> top3Messages,
                @JsonProperty("top3Users") List<String> top3Users,
                @JsonProperty("lowActivityUsers") List<String> lowActivityUsers,
                @JsonProperty("topMessagesForTopics") List<String> topMessagesForTopics)
    {
        this.startUTC = startUTC;
        this.endUTC = endUTC;
        this.numNewMessages = numNewMessages;
        this.numMessageReads = numMessageReads;
        this.avgReads = avgReads;
        this.avgNewMessageReads = avgNewMessageReads;
        this.top3Messages = top3Messages;
        this.top3Users = top3Users;
        this.lowActivityUsers = lowActivityUsers;
        this.topMessagesForTopics = topMessagesForTopics;
    }

    @JsonProperty
    public long getStartUTC() {
        return startUTC;
    }

    @JsonProperty
    public long getEndUTC() {
        return endUTC;
    }

    @JsonProperty
    public long getNumNewMessages() {
        return numNewMessages;
    }

    @JsonProperty
    public long getNumMessageReads() {
        return numMessageReads;
    }

    @JsonProperty
    public long getAvgReads() {
        return avgReads;
    }

    @JsonProperty
    public long getAvgNewMessageReads() {
        return avgNewMessageReads;
    }

    @JsonProperty
    public List<String> getTop3Messages() {
        return top3Messages;
    }

    @JsonProperty
    public List<String> getTop3Users() {
        return top3Users;
    }

    @JsonProperty
    public List<String> getLowActivityUsers() {
        return lowActivityUsers;
    }

    @JsonProperty
    public List<String> getTopMessagesForTopics() {
        return topMessagesForTopics;
    }
}
