package frontier.engine.input;

public class MouseButtonBinding implements InputBinding {
    private final MouseButton button;

    public MouseButtonBinding(MouseButton button) {
        this.button = button;
    }

    @Override
    public boolean isDown(Input input) {
        return input.getMouse().isButtonDown(button);
    }

    @Override
    public boolean isHeld(Input input) {
        return input.getMouse().isButtonHeld(button);
    }

    @Override
    public boolean isUp(Input input) {
        return input.getMouse().isButtonUp(button);
    }
}
