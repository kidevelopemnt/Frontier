package frontier.game;

import frontier.engine.Engine;
import frontier.engine.core.Logger;
import frontier.engine.game.IGame;
import frontier.engine.graphics.Mesh;

public class FrontierGame implements IGame {

    private Engine engine;

    @Override
    public void initialize(Engine engine) {
        this.engine = engine;
        engine.getRenderer().setFillColor(.1f, .2f, .1f, 1.0f);
    }

    @Override
    public void update(float deltaTime) {
        // engine.getRenderer().render(mesh, material, transform);
    }

    @Override
    public void shutdown() {

    }
}
