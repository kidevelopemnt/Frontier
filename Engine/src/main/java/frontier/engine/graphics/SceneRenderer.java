package frontier.engine.graphics;

import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.LightComponent;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public class SceneRenderer {
    private Renderer renderer;

    public SceneRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void render(Scene scene) {
        Entity directionalLight = null; // TODO: Temporary
        Entity pointLight = null;

        Camera camera = scene.getCamera();

        if (camera == null) {
            return;
        }

        List<Entity> lights = findLights(scene);
        for (Entity entity : lights) {

            LightComponent light =
                    entity.getComponent(LightComponent.class);

            Transform transform =
                    entity.getTransform();

            switch (light.getLightType()) {

                case DIRECTIONAL:
                    directionalLight = entity;
                    break;

                case POINT:
                    pointLight = entity;
                    break;
            }
        }

        for (Entity entity : scene.getEntities()) {
            MeshRenderer meshRenderer = entity.getComponent(MeshRenderer.class);

            if (!entity.hasComponent(MeshRenderer.class) || !entity.isEnabled()) {
                continue;
            }

            renderer.render(meshRenderer.getMesh(), meshRenderer.getMaterial(), entity.getTransform(), camera, directionalLight, pointLight);
        }
    }

    private List<Entity> findLights(Scene scene) {

        List<Entity> lights =
                new ArrayList<>();

        for (Entity entity : scene.getEntities()) {

            if (entity.hasComponent(
                    LightComponent.class
            ) && entity.isEnabled()) {

                lights.add(entity);
            }
        }

        return lights;
    }
}
