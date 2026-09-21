package frontier.engine.scene;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;

import java.util.*;

public class Scene {
    private String name;
    private String filepath;
    private Map<UUID, Entity> entities = new HashMap<>();
    private Engine engine;

    public Scene(String name, String filepath, Engine engine, boolean addCamera) {
        this.name = name;
        this.filepath = filepath;
        this.engine = engine;

        if (addCamera) {
            Entity camera = new Entity("Main Camera", this);
            camera.addComponent(Camera.class);
            addEntity(camera);
        }
    }

    public Scene(String name, String filepath, Engine engine) {
        this(name, filepath, engine, true);
    }

    public Scene(String name, Engine engine) {
        this(name, name + ".scene", engine);
    }

    public void update(float deltaTime) {
        for (Entity e : entities.values()) {
            e.update(deltaTime);
        }
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getID(), entity);
    }

    public Collection<Entity> getEntities() {
        return entities.values();
    }

    public Entity findEntity(String name) {
        for (Entity entity : getEntities()) {  // TEMPORARY:
            if (entity.getName().equals("cube")) {
                return entity;
            }
        }
        return null;
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

    public Engine getEngine() {
        return engine;
    }
}
