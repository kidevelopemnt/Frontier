package frontier.engine.input;

import java.lang.reflect.InvocationTargetException;
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

    public boolean isDown(String name) {
        return registry.get(name).isDown(input);
    }

    public boolean isHeld(String name) {
        return registry.get(name).isHeld(input);
    }

    public boolean isUp(String name) {
        return registry.get(name).isUp(input);
    }
}
