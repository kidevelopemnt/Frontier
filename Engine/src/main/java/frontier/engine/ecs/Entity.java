package frontier.engine.ecs;

import frontier.engine.ecs.components.Component;
import frontier.engine.ecs.components.TransformComponent;
import frontier.engine.graphics.Transform;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity {
    private UUID id;
    private String name;
    private Map<Class<? extends Component>, Component> components = new HashMap<>();
    // private Scene scene;

    public Entity (String name) {
        this.id = UUID.randomUUID();
        this.name = name;

        components.put(TransformComponent.class, new TransformComponent());
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

    public <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
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

    public void cleanup() {
        for (Component c : components.values()) {
            c.cleanup();
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", id.toString());
        data.put("name", name);
        // data.put("__TransformComponent", getTransformComponent().serialize());

        for (Component component : components.values()) {
            data.put("__" + component.getClass().getSimpleName(), component.serialize());
        }

        // TODO: Serialize all components
        // And put "__component": "TransformComponent"

        return data;
    }
}
