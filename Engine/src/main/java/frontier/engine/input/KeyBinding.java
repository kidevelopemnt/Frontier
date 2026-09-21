package frontier.engine.input;

public class KeyBinding implements InputBinding {
    private final Key key;

    public KeyBinding(Key key) {
        this.key = key;
    }

    @Override
    public boolean isDown(Input input) {
        return input.isKeyDown(key);
    }

    @Override
    public boolean isHeld(Input input) {
        return input.isKeyHeld(key);
    }

    @Override
    public boolean isUp(Input input) {
        return input.isKeyUp(key);
    }
}
