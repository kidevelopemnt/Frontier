package frontier.engine.game;

import frontier.engine.Engine;

public interface IGame {
    void initialize(Engine engine);

    void update(float deltaTime);

    void shutdown();
}
