package frontier.game;

import frontier.engine.application.Application;
import frontier.engine.application.ApplicationConfiguration;
import frontier.engine.core.Logger;

public class Frontier {

    public static void main(String[] args) {
        ApplicationConfiguration appConfig = new ApplicationConfiguration();
        appConfig.logMode = Logger.LogMode.CONSOLE;
        // appConfig.logFile = "log.txt";

        Application application = new Application(appConfig, new FrontierGame());
        application.run();
    }
}