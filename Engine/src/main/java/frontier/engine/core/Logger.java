package frontier.engine.core;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    public enum LogMode {
        CONSOLE,
        FILE
    }

    public enum LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        CRITICAL
    }

    private LogMode logMode = LogMode.CONSOLE;
    private LogLevel minLogLevel = LogLevel.ERROR;

    private String logFilepath;

    // TODO: Log to file

    private void output(String message) {
        if (logMode == LogMode.CONSOLE) {
            System.out.println(message);
        } else if (logMode == LogMode.FILE) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(logFilepath, true);
                fw.write(message + "\n");
            } catch (IOException e) {
                logError("IOException: " + e.getMessage());
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e) {
                    logError("IOException: " + e.getMessage());
                }
            }
        }
    }

    private void log(String message, LogLevel logLevel) {
        if (minLogLevel.compareTo(logLevel) <= 0) {
            return;
        }

        switch (logLevel) {
            case LogLevel.DEBUG: output("[DEBUG] " + message); break;
            case LogLevel.INFO: output("[INFO] " + message); break;
            case LogLevel.WARN: output("[WARN] " + message); break;
            case LogLevel.ERROR: output("[ERROR] " + message); break;
            case LogLevel.CRITICAL: output("[CRITICAL] " + message); break;
        }
    }

    public void logDebug(String message) {
        log(message, LogLevel.DEBUG);
    }

    public void logInfo(String message) {
        log(message, LogLevel.INFO);
    }

    public void logWarn(String message) {
        log(message, LogLevel.WARN);
    }

    public void logError(String message) {
        log(message, LogLevel.ERROR);
    }

    public void logCritical(String message) {
        log(message, LogLevel.CRITICAL);
    }

    public void setLogMode(LogMode logMode) {
        this.logMode = logMode;
    }

    public void setLogFile(String filepath) {
        this.logFilepath = filepath;
    }

    public void setMinLogLevel(LogLevel logLevel) {
        this.minLogLevel = logLevel;
    }
}
