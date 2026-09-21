package frontier.engine.scene;

import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;

import java.util.*;

public class Scene {
    private String name;
    private String filepath;
    private Map<UUID, Entity> entities = new HashMap<>();

    public Scene(String name, String filepath, boolean addCamera) {
        this.name = name;
        this.filepath = filepath;

        if (addCamera) {
            Entity camera = new Entity("Main Camera");
            camera.addComponent(Camera.class);
            addEntity(camera);
        }
    }

    public Scene(String name, String filepath) {
        this(name, filepath, true);
    }

    public Scene(String name) {
        this(name, name + ".scene");
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getID(), entity);
    }

    public Collection<Entity> getEntities() {
        return entities.values();
    }

    public Camera getCamera() {
        for (Entity entity : getEntities()) {
            if (entity.hasComponent(Camera.class) && entity.isEnabled()) {
                return entity.getComponent(Camera.class);
            }
        }

        return null;
    }

    public void destroyEntity(Entity entity) {
        entity.cleanup();
        entities.remove(entity.getID());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("name", name);

        List<Map<String, Object>> entities = new ArrayList<>();

        for (Entity entity : getEntities()) {
            entities.add(entity.serialize());
        }

        data.put("entities", entities);

        return data;
    }

    public String getFilepath() {
        return filepath;
    }
}
