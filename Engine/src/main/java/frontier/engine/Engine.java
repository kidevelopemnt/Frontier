package frontier.engine;

import frontier.engine.application.Application;
import frontier.engine.application.ApplicationConfiguration;
import frontier.engine.graphics.Renderer;
import frontier.engine.core.Logger;
import frontier.engine.core.Time;
import frontier.engine.graphics.lighting.DirectionalLight;
import frontier.engine.graphics.lighting.PointLight;

public class Engine {
    private Logger logger;
    private Renderer renderer;
    private Time time;

    public GameObject object;  // TODO: Add scenes, these are Temporary!
    public DirectionalLight directionalLight;
    public PointLight pointLight;

    private Application application;

    public Engine (Application application) {
        this.application = application;
    }

    public void initialize(ApplicationConfiguration config) {
        logger = new Logger();
        renderer = new Renderer();
        time = new Time();

        logger.setLogMode(config.logMode);
        logger.setLogFile(config.logFile);

        renderer.initialize(this);
        logger.logInfo("Engine initialized.");
    }

    public void update(double deltaTime) {

    }

    public void render() {
        renderer.beginFrame();


        renderer.render(object);

        renderer.endFrame();
    }

    public void shutdown() {
        renderer.shutdown();
        logger.logInfo("Engine shutdown.");
    }

    public Application getApp() {
        return application;
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
