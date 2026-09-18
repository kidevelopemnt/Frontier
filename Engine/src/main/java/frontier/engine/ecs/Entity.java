package frontier.engine.ecs;

import frontier.engine.ecs.components.Component;
import frontier.engine.ecs.components.TransformComponent;
import frontier.engine.graphics.Material;
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

    public <T extends Component> T addComponent(Class<T> c) {
        try {
            Component component = c.getDeclaredConstructor().newInstance();
            component.setEntity(this);
            components.put(c, component);
            return c.cast(component);
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

    public UUID getID() {
        return id;
    }

    public void cleanup() {
        for (Component c : components.values()) {
            c.cleanup();
        }
    }
}
