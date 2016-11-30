package edu.ucsb.engineering.buzmo.util;

import java.util.Date;

public class TimeKeeper {
    private long start; //game time start
    private long upStart; //system clock to compute uptime

    public TimeKeeper(long start) {
        this.start = start;
        this.upStart = (new Date()).getTime();
    }

    public long getTime() {
        return this.start + ((new Date()).getTime() - this.upStart);
    }

    public void setTime(long utc) {
        this.start = utc;
        this.upStart = (new Date()).getTime();
    }
}
