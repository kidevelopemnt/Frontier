package frontier.game.input;

import frontier.engine.input.InputAction;
import frontier.engine.input.Key;
import frontier.engine.input.KeyBinding;

public class ForwardAction extends InputAction {
    public ForwardAction() {
        addBinding(new KeyBinding(Key.W));
        addBinding(new KeyBinding(Key.UP));
    }
}
