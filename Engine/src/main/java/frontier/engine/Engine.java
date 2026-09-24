package frontier.engine;

import frontier.engine.application.Application;
import frontier.engine.application.ApplicationConfiguration;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.graphics.DebugRenderer;
import frontier.engine.graphics.Renderer;
import frontier.engine.core.Logger;
import frontier.engine.core.Time;
import frontier.engine.graphics.SceneRenderer;
import frontier.engine.graphics.lighting.DirectionalLight;
import frontier.engine.graphics.lighting.PointLight;
import frontier.engine.input.ActionRegistry;
import frontier.engine.input.Input;
import frontier.engine.physics.Physics;
import frontier.engine.scene.Scene;
import frontier.engine.scene.SceneSerializer;
import org.apache.commons.io.FilenameUtils;

public class Engine {
    private Logger logger;
    private Input input;
    private Physics physics;
    private Renderer renderer;
    private SceneRenderer sceneRenderer;
    private Time time;

    private Application application;
    private SceneSerializer sceneSerializer;
    private Scene activeScene;

    public Engine (Application application) {
        this.application = application;
    }

    public void initialize(ApplicationConfiguration config) {
        logger = new Logger();
        input = new Input(this, application.getMainWindow());
        renderer = new Renderer();
        sceneRenderer = new SceneRenderer(renderer);
        sceneSerializer = new SceneSerializer(this);
        time = new Time();

        logger.setLogMode(config.logMode);
        logger.setLogFile(config.logFile);

        renderer.initialize(this);
        logger.logInfo("Engine initialized.");
    }

    public Scene createScene(String name) {
        return new Scene(name, this);
    }

    public void setActiveScene(Scene scene) {
        activeScene = scene;
        physics = new Physics(this, activeScene);
    }

    public void loadScene(String filepath) {
        setActiveScene(new Scene(FilenameUtils.getBaseName(filepath), filepath, this, false));
        sceneSerializer.load(activeScene, getApp().getProjectDirectory().resolve("src/main/resources/scenes").resolve(filepath));
    }

    public void saveScene() {
        sceneSerializer.save(activeScene);
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public void update(double deltaTime) {
        input.update();
        activeScene.update((float) deltaTime);
        // Game.update();
    }

    public void endFrame() {
        input.endFrame();
    }

    public void render() {
        renderer.beginFrame();
        if (activeScene != null) {
            sceneRenderer.render(activeScene);
            DebugRenderer.render(activeScene.getCamera(), application.getMainWindow().getSize().x / application.getMainWindow().getSize().y); // TODO: getAspectRatio()
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
        return activeScene.getCamera();
    }

    public Time getTime() {
        return time;
    }

    public Physics getPhysics() {
        return physics;
    }
}
