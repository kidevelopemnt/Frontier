package frontier.engine.ecs.components;

import frontier.engine.ecs.Entity;

public abstract class Component {
    private Entity entity;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void initialize() {

    }

    public void update(float deltaTime) {

    }

    public void cleanup() {

    }
}
