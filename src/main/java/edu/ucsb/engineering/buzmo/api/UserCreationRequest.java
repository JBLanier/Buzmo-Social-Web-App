package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.Principal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreationRequest extends User {

    private String passwd;

    @JsonCreator
    public UserCreationRequest(@JsonProperty("userid") long userid, @JsonProperty("full_name") String full_name,
                @JsonProperty("email") String email, @JsonProperty("screenname") String screenname,
                @JsonProperty("phone") long phone, @JsonProperty("isManager") boolean is_manager,
                @JsonProperty("passwd") String passwd) {
        super(userid, full_name, email, screenname, phone, is_manager);
        this.passwd = passwd;
    }


    @JsonProperty
    public String getPasswd() {
        return passwd;
    }

}