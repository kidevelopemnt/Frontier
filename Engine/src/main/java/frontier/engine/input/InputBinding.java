package frontier.engine.input;

public interface InputBinding {
    boolean isDown(Input input);
    boolean isHeld(Input input);
    boolean isUp(Input input);
}
