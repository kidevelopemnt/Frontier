package frontier.engine.input;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {
    private Input input;
    private Map<String, InputAction> registry = new HashMap<>();

    public ActionRegistry(Input input) {
        this.input = input;
    }

    public void register(String name, InputAction action) {
        registry.put(name, action);
    }

    private InputAction getAction(String name) {
        InputAction action = registry.get(name);

        if (action == null) {
            throw new IllegalArgumentException(
                    "Input action is not registered: " + name
            );
        }

        return action;
    }

    public boolean isDown(String name) {
        return getAction(name).isDown(input);
    }

    public boolean isHeld(String name) {
        return getAction(name).isHeld(input);
    }

    public boolean isUp(String name) {
        return getAction(name).isUp(input);
    }
}
