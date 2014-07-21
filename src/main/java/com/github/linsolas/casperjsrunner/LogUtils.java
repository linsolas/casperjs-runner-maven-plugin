package com.github.linsolas.casperjsrunner;

import org.apache.maven.plugin.logging.Log;

public class LogUtils {

    private static Logger logger;

    public static void setLog(Log log, boolean verbose) {
        logger = new Logger(log, verbose);
    }

    public static Logger getLogger() {
        return logger;
    }
}

class Logger {

    private Log log;

    private boolean verbose;

    public Logger(Log log, boolean verbose) {
        this.log = log;
        this.verbose = verbose;
    }

    public void debug(CharSequence msg) {
        if (verbose) {
            log.info(msg);
        }
    }

    public void info(CharSequence msg) {
        log.info(msg);
    }

    public void warn(CharSequence msg) {
        log.warn(msg);
    }

    public void error(CharSequence msg, Throwable error) {
        log.error(msg, error);
    }
}