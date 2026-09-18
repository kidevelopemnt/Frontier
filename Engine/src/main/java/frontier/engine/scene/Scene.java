package frontier.engine.scene;

import frontier.engine.ecs.Entity;

import java.util.*;

public class Scene {
    private Map<UUID, Entity> entities = new HashMap<>();

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
