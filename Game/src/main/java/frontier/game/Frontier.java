package frontier.game;

import frontier.engine.application.Application;
import frontier.engine.application.ApplicationConfiguration;
import frontier.engine.core.Logger;

import java.nio.file.Paths;

public class Frontier {

    public static void main(String[] args) {
        ApplicationConfiguration appConfig = new ApplicationConfiguration();
        appConfig.logMode = Logger.LogMode.CONSOLE;
        // appConfig.logFile = "log.txt";

        // TODO: This path is currently a placeholder for while I have Intellij open at the Frontier directory
        // Once I make the editor, when I open a project it will use the ProjectName.project file to get resourceDirectory,
        // Source directory, etc.
        Application application = new Application(Paths.get("").toAbsolutePath().resolve("Game"), appConfig, new FrontierGame());
        application.run();
    }
}