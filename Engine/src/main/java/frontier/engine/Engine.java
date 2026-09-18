package frontier.engine;

import frontier.engine.application.Application;
import frontier.engine.application.ApplicationConfiguration;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.graphics.Renderer;
import frontier.engine.core.Logger;
import frontier.engine.core.Time;
import frontier.engine.graphics.SceneRenderer;
import frontier.engine.graphics.lighting.DirectionalLight;
import frontier.engine.graphics.lighting.PointLight;
import frontier.engine.input.Input;
import frontier.engine.scene.Scene;

public class Engine {
    private Logger logger;
    private Input input;
    private Renderer renderer;
    private SceneRenderer sceneRenderer;
    private Time time;

    private Application application;
    private Scene activeScene;

    public Engine (Application application) {
        this.application = application;
    }

    public void initialize(ApplicationConfiguration config) {
        logger = new Logger();
        input = new Input(application.getMainWindow());
        renderer = new Renderer();
        sceneRenderer = new SceneRenderer(renderer);
        time = new Time();

        logger.setLogMode(config.logMode);
        logger.setLogFile(config.logFile);

        renderer.initialize(this);
        logger.logInfo("Engine initialized.");
    }

    public void loadScene(Scene scene) {
        activeScene = scene;
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public void update(double deltaTime) {
        input.update();
        // Game.update();
        input.endFrame();
    }

    public void render() {
        renderer.beginFrame();
        if (activeScene != null) {
            sceneRenderer.render(activeScene);
        }
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

    public Input getInput() {
        return input;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Camera getCamera() {
        return sceneRenderer.findCamera(activeScene);
    }


    public Time getTime() {
        return time;
    }
}
