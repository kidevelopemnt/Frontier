package frontier.engine;

import frontier.engine.application.ApplicationConfiguration;
import frontier.engine.graphics.Renderer;
import frontier.engine.core.Logger;
import frontier.engine.core.Time;

public class Engine {
    private Logger logger;
    private Renderer renderer;
    private Time time;

    public void initialize(ApplicationConfiguration config) {
        logger = new Logger();
        renderer = new Renderer();
        time = new Time();

        logger.setLogMode(config.logMode);
        logger.setLogFile(config.logFile);

        renderer.initialize();
        logger.logInfo("Engine initialized.");
    }

    public void update(double deltaTime) {

    }

    public void render() {
        renderer.clear();
    }

    public void shutdown() {
        renderer.shutdown();
        logger.logInfo("Engine shutdown.");
    }

    public Logger getLogger() {
        return logger;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Time getTime() {
        return time;
    }
}
