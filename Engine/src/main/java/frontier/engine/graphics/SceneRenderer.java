package frontier.engine.graphics;

import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.scene.Scene;

public class SceneRenderer {
    private Renderer renderer;

    public SceneRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void render(Scene scene) {
        for (Entity entity : scene.getEntities()) {
            MeshRenderer meshRenderer = entity.getComponent(MeshRenderer.class);

            if (meshRenderer == null) {
                continue;
            }

            renderer.render(meshRenderer.getMesh(), meshRenderer.getMaterial(), entity.getTransform());
        }
    }
}
