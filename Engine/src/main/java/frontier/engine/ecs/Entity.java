package frontier.engine.ecs;

import frontier.engine.ecs.components.Component;
import frontier.engine.ecs.components.TransformComponent;
import frontier.engine.graphics.Transform;
import frontier.engine.scene.Scene;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity {
    private UUID id;
    private String name;
    private Map<Class<? extends Component>, Component> components = new HashMap<>();
    private Scene scene;

    private boolean isEnabled = true;

    public Entity (String name, Scene scene) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.scene = scene;

        components.put(TransformComponent.class, new TransformComponent());
    }

    public void update(float deltaTime) {
        for (Component c : components.values()) {
            c.update(deltaTime);
        }
    }

    public boolean hasComponent(Class<?> type) {
        return components.get(type) != null;
    }

    public <T extends Component> T addComponent(Class<?> c) {
        try {
            Component component = (Component) c.getDeclaredConstructor().newInstance();
            component.setEntity(this);
            components.put((Class<? extends Component>) c, component);
            return (T) c.cast(component);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeComponent(Class<?> c) {
        if (hasComponent(c)) {
            getComponent(c).cleanup();
            components.remove(c);
        }
    }

    public <T extends Component> T getComponent(Class<?> type) {
        return (T) type.cast(components.get(type));
    }

    public TransformComponent getTransformComponent() {
        return getComponent(TransformComponent.class);
    }

    public Transform getTransform() {
        return getTransformComponent().getTransform();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getID() {
        return id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void cleanup() {
        for (Component c : components.values()) {
            c.cleanup();
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", id.toString());
        data.put("name", name);
        data.put("isEnabled", isEnabled);

        for (Component component : components.values()) {
            data.put("__" + component.getClass().getSimpleName(), component.serialize());
        }

        // TODO: Serialize all components
        // And put "__component": "TransformComponent"

        return data;
    }

    public Scene getScene() {
        return scene;
    }
}
