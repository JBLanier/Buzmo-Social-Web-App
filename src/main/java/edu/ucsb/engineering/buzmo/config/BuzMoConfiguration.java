package edu.ucsb.engineering.buzmo.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;

public class BuzMoConfiguration extends Configuration {

    @NotNull
    private DBConfig dbConfig;

    private long startTime;

    @JsonProperty
    public DBConfig getDbConfig() {
        return dbConfig;
    }

    @JsonProperty
    public void setDbConfig(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @JsonProperty
    public long getStartTime() {
        return startTime;
    }

    @JsonProperty
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
