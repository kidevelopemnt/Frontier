package frontier.engine.application;

import frontier.engine.core.Logger;

import java.nio.file.Path;

public class ApplicationConfiguration {
    public Logger.LogMode logMode = Logger.LogMode.CONSOLE;
    public Logger.LogLevel logLevel = Logger.LogLevel.ERROR;
    public String logFile = "";
}
