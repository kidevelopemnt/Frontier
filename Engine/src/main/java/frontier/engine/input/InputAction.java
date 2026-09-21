package frontier.engine.input;

import java.util.ArrayList;
import java.util.List;

public class InputAction {
    private final List<InputBinding> bindings = new ArrayList<>();

    public InputAction addBinding(InputBinding binding) {
        bindings.add(binding);

        return this;
    }

    public boolean isDown(Input input) {
        for (InputBinding binding : bindings) {
            if (binding.isDown(input)) {
                return true;
            }
        }

        return false;
    }

    public boolean isHeld(Input input) {
        for (InputBinding binding : bindings) {
            if (binding.isHeld(input)) {
                return true;
            }
        }

        return false;
    }

    public boolean isUp(Input input) {
        for (InputBinding binding : bindings) {
            if (binding.isUp(input)) {
                return true;
            }
        }

        return false;
    }
}
