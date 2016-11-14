package edu.ucsb.engineering.buzmo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private long userid;
    private String name;
    private String email;
    private String screenname;
    private long phone;
    private boolean is_manager;

    @JsonCreator
    public User(@JsonProperty("userid") long userid, @JsonProperty("name") String name,
                @JsonProperty("email") String email, @JsonProperty("screenname") String screenname,
                @JsonProperty("phone") long phone, @JsonProperty("is_manager") boolean is_manager) {
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.screenname = screenname;
        this.phone = phone;
        this.is_manager = is_manager;
    }

    @JsonProperty
    public long getUserid() {
        return userid;
    }

    @JsonProperty
    public String getName() {
        return name;
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

    @JsonProperty
    public boolean is_manager() {
        return is_manager;
    }
}
