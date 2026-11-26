package frontier.game;

import frontier.engine.Engine;
import frontier.engine.game.IGame;

import frontier.engine.input.Input;
import frontier.engine.input.InputAction;
import frontier.engine.input.Key;
import frontier.engine.input.KeyBinding;
import frontier.engine.physics.CollisionLayerManager;
import frontier.engine.physics.Ray;
import frontier.engine.physics.RaycastHit;
import frontier.game.input.Actions;
import frontier.game.scenes.PhysicsTestScene;
import frontier.game.scenes.PlaygroundScene;
import org.joml.Vector3f;

public class FrontierGame implements IGame {
    private Engine engine;

    @Override
    public void initialize(Engine engine) {
        this.engine = engine;
        engine.getRenderer().setFillColor(.4392f, .502f, .5647f, 1.0f);
        engine.getInput().getMouse().setCursorLocked(true);

        setupInput();
        setupCollisionLayers();

        // Once I add an editor, the editor will handle creating scenes
        // Once I add an editor, I will use the save/load scene methods instead of this

        PlaygroundScene scene = new PlaygroundScene(engine);
        // PhysicsTestScene scene = new PhysicsTestScene(engine);

        engine.setActiveScene(scene.getScene());
        scene.initialize();
    }

    private void setupInput() {
        engine.getInput().registerAction(Actions.FORWARD, new InputAction().addBinding(new KeyBinding(Key.W)));
        engine.getInput().registerAction(Actions.BACK, new InputAction().addBinding(new KeyBinding(Key.S)));
        engine.getInput().registerAction(Actions.LEFT, new InputAction().addBinding(new KeyBinding(Key.A)));
        engine.getInput().registerAction(Actions.RIGHT, new InputAction().addBinding(new KeyBinding(Key.D)));
    }

    private void setupCollisionLayers() {
        CollisionLayerManager layerManager = engine.getPhysics().getCollisionLayers();

        layerManager.addLayer("player");
        layerManager.addLayer("props");
        layerManager.setCollision("player", "world", true);
        layerManager.setCollision("player", "props", true);
        layerManager.setCollision("props", "world", true);
    }

    @Override
    public void update(float deltaTime) {
        if (engine.getInput().isKeyDown(Key.ESCAPE)) {
            shutdown();
        }
    }

    @Override
    public void shutdown() {
        engine.shutdown();
    }
}
