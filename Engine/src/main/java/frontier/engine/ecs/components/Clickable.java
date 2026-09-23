package frontier.engine.ecs.components;

import frontier.engine.input.Mouse;
import frontier.engine.input.MouseButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Clickable extends Component {
    private List<MouseButton> buttons;

    public Clickable(List <MouseButton> buttons) {
        this.buttons = buttons;
    }

    public Clickable() {
        this(List.of(MouseButton.LEFT));
    }

    @Override
    public Map<String, Object> serialize() {
        List<String> serializedButtons = new ArrayList<>();
        for (MouseButton button : buttons) {
            serializedButtons.add(button.name());
        }

        return Map.of("buttons", serializedButtons);
    }
}
