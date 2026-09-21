package frontier.game;

import frontier.engine.Engine;
import frontier.engine.game.IGame;

import frontier.engine.input.Input;
import frontier.engine.input.InputAction;
import frontier.engine.input.Key;
import frontier.engine.input.KeyBinding;
import frontier.game.input.Actions;
import frontier.game.scenes.PlaygroundScene;

public class FrontierGame implements IGame {
    private Engine engine;

    @Override
    public void initialize(Engine engine) {
        this.engine = engine;
        engine.getRenderer().setFillColor(.1f, .2f, .1f, 1.0f);
        engine.getInput().getMouse().setCursorLocked(true);

        setupInput();

        // Once I add an editor, the editor will handle creating scenes
        // Once I add an editor, I will use the save/load scene methods instead of this
        PlaygroundScene playground = new PlaygroundScene(engine);
        playground.initialize();
        engine.setActiveScene(playground.getScene());
    }

    private void setupInput() {
        engine.getInput().registerAction(Actions.FORWARD, new InputAction().addBinding(new KeyBinding(Key.W)));
        engine.getInput().registerAction(Actions.BACK, new InputAction().addBinding(new KeyBinding(Key.S)));
        engine.getInput().registerAction(Actions.LEFT, new InputAction().addBinding(new KeyBinding(Key.A)));
        engine.getInput().registerAction(Actions.RIGHT, new InputAction().addBinding(new KeyBinding(Key.D)));
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void shutdown() {

    }
}
