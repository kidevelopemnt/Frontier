package frontier.engine.core;

public class Time {
    private float deltaTime;
    private float fps;

    public void update(float deltaTime) {
        this.deltaTime = deltaTime;
        this.fps = 1 / deltaTime;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getFPS() {
        return fps;
    }
}
