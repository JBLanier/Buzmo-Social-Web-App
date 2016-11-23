package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MC_MSG_Search {

    private List<String> topics;

    @JsonProperty
    public List<String> getTopics() {
        return topics;
    }

    @JsonProperty
    public void setTopics(List<String> topics) { this.topics = topics; }

}
