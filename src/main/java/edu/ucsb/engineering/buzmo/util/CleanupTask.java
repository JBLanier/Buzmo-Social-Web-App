package edu.ucsb.engineering.buzmo.util;

import edu.ucsb.engineering.buzmo.daos.ChatGroupsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.TimerTask;

/**
 * ChatGroup Duration cleanup
 */
public class CleanupTask extends TimerTask{

    final static Logger logger = LoggerFactory.getLogger(CleanupTask.class);

    private TimeKeeper tk;
    private ChatGroupsDAO dao;

    public CleanupTask(TimeKeeper tk, ChatGroupsDAO dao) {
        this.tk = tk;
        this.dao = dao;
    }

    @Override
    public void run() {
        logger.info("Running chat group message cleanup.");
        try {
            this.dao.cleanup(this.tk.getTime());
        } catch (SQLException e) {
            logger.warn("Could not cleanup old group chat messages.", e);
        }
    }
}
