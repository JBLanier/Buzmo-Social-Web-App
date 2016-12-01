package edu.ucsb.engineering.buzmo.time;

import java.util.TimerTask;

public class TimeKeeperTask extends TimerTask {

    private TimeKeeper tk;

    public TimeKeeperTask(TimeKeeper tk) {
        this.tk = tk;
    }

    @Override
    public void run() {
        this.tk.saveTime();
    }
}
