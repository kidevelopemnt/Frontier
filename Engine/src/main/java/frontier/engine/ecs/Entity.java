package frontier.engine.ecs;

import frontier.engine.ecs.components.Component;
import frontier.engine.ecs.components.TransformComponent;
import frontier.engine.ecs.components.physics.Collider;
import frontier.engine.graphics.Transform;
import frontier.engine.scene.Scene;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Entity {
    private UUID id;
    private String name;
    private Map<Class<? extends Component>, Component> components = new HashMap<>();
    private Scene scene;

    private Entity parent;
    private List<Entity> children = new ArrayList<>();

    private List<String> groups = new ArrayList<>();  // Entities can be filtered by groups in Raycasts or other

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

    public void setParent(Entity parent) {
        if (this.parent == parent) {
            return;
        }

        // Prevent an entity from becoming its own parent.
        if (parent == this) {
            throw new IllegalArgumentException(
                    "An entity cannot be its own parent."
            );
        }

        // Prevent circular hierarchies.
        if (parent != null && isAncestorOf(parent)) {
            throw new IllegalArgumentException(
                    "Cannot create a circular entity hierarchy."
            );
        }

        // Remove from current parent.
        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        this.parent = parent;

        // Add to new parent.
        if (this.parent != null && !this.parent.children.contains(this)) {
            this.parent.children.add(this);
        }

        updateTransformParent();
    }

    private boolean isDescendantOf(Entity entity) {
        Entity current = this;

        while (current != null) {
            if (current == entity) {
                return true;
            }

            current = current.parent;
        }

        return false;
    }

    private boolean isAncestorOf(Entity entity) {
        Entity current = entity;

        while (current != null) {
            if (current == this) {
                return true;
            }

            current = current.parent;
        }

        return false;
    }

    private void updateTransformParent() {
        TransformComponent transform = getComponent(TransformComponent.class);

        if (transform == null) {
            return;
        }

        if (parent == null) {
            transform.getTransform().setParent(null);
            return;
        }

        TransformComponent parentTransform =
                parent.getComponent(TransformComponent.class);

        if (parentTransform == null) {
            transform.getTransform().setParent(null);
            return;
        }

        transform.getTransform().setParent(parentTransform.getTransform());
    }

    public Entity getParent() {
        return parent;
    }

    public List<Entity> getChildren() {
        return children;
    }

    public boolean hasComponent(Class<?> type) {
        return components.get(type) != null;
    }

    public <T extends Component> T addComponent(Class<?> c) {
        try {
            Component component = (Component) c.getDeclaredConstructor().newInstance();
            component.setEntity(this);
            component.initialize();
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

    public <T extends Collider> T getCollider() {
        for (Component c : components.values()) {
            if (c instanceof Collider) {
                return (T) c;
            }
        }

        return null;
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

    public List<String> getGroups() {
        return groups;
    }

    public void addGroup(String group) { groups.add(group); }
}
