package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.Principal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreationRequest extends User {

    private String passwd;
    private List<String> topics;

    @JsonCreator
    public UserCreationRequest(@JsonProperty("full_name") String full_name,
                               @JsonProperty("email") String email, @JsonProperty("screenname") String screenname,
                               @JsonProperty("phone") long phone,
                               @JsonProperty("passwd") String passwd, @JsonProperty("topics") List<String> topics) {
        super(-1, full_name, email, screenname, phone, false);
        this.passwd = passwd;
        this.topics = topics;
    }

    @JsonProperty
    public String getPasswd() {
        return passwd;
    }

    @JsonProperty
    public List<String> getTopics() {
        return topics;
    }
}