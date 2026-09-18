package frontier.engine.scene;

import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;

import java.util.*;

public class Scene {
    private Map<UUID, Entity> entities = new HashMap<>();

    public Scene() {
        Entity camera = new Entity("Main Camera");
        camera.addComponent(Camera.class);
        addEntity(camera);
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getID(), entity);
    }

    public Collection<Entity> getEntities() {
        return entities.values();
    }

    public void destroyEntity(Entity entity) {
        entity.cleanup();
        entities.remove(entity.getID());
    }
}
