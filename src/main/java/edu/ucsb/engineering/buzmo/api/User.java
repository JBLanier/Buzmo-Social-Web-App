package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.Principal;

public class User implements Principal {
    private long userid;
    private String full_name;
    private String email;
    private String screenname;
    private long phone;
    private boolean is_manager;

    @JsonCreator
    public User(@JsonProperty("userid") long userid, @JsonProperty("full_name") String full_name,
                @JsonProperty("email") String email, @JsonProperty("screenname") String screenname,
                @JsonProperty("phone") long phone, @JsonProperty("isManager") boolean is_manager) {
        this.userid = userid;
        this.full_name = full_name;
        this.email = email;
        this.screenname = screenname;
        this.phone = phone;
        this.is_manager = is_manager;
    }

    @JsonProperty
    public long getUserid() {
        return userid;
    }

    @JsonProperty("full_name")
    public String getName() {
        return full_name;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public String getScreenname() {
        return screenname;
    }

    @JsonProperty
    public long getPhone() {
        return phone;
    }

    @JsonProperty("isManager")
    public boolean isManager() {
        return is_manager;
    }
}
