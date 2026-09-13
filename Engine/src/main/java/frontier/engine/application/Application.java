package frontier.engine.application;

import frontier.engine.Engine;
import frontier.engine.game.IGame;

public class Application {
    private final Engine engine;
    private final Window mainWindow;
    private final IGame game;
    private final ApplicationConfiguration config;

    double lastTime = System.nanoTime() / 1e9;

    public Application(ApplicationConfiguration config, IGame game) {
        this.engine = new Engine();
        this.mainWindow = new Window();
        this.game = game;
        this.config = config;
    }

    public void run() {
        initialize();
        loop();
        shutdown();
    }

    private void initialize() {
        engine.initialize(config);

        mainWindow.create();
        game.initialize(engine);
    }

    private void loop() {
        while (!mainWindow.shouldClose()) {
            double currentTime = System.nanoTime() / 1e9;
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;
            engine.getTime().update(deltaTime);

            mainWindow.update();
            engine.update(deltaTime);
            game.update(deltaTime);
            engine.render();
            mainWindow.draw();
        }
    }

    private void shutdown() {
        game.shutdown();
        mainWindow.destroy();
        engine.shutdown();
    }
}
