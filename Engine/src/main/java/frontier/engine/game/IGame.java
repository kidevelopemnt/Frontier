package frontier.engine.game;

import frontier.engine.Engine;

import java.lang.reflect.InvocationTargetException;

public interface IGame {
    void initialize(Engine engine);

    void update(float deltaTime);

    void shutdown();
}
