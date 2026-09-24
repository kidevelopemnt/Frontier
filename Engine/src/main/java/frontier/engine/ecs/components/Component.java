package frontier.engine.ecs.components;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class Component {
    protected Entity entity;

    public abstract Map<String, Object> serialize();

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public Engine getEngine() {
        return entity.getScene().getEngine();
    }

    public void initialize() {

    }

    public void update(float deltaTime) {

    }

    public void cleanup() {

    }

    public void load(Map<String, Object> data) {
        data.forEach((key, val) -> {
            try {
                Field field = getClass().getDeclaredField(key);
                field.setAccessible(true);

                // Convert the value to match the field's type if necessary
                Object coercedValue = coerceType(field.getType(), val);

                field.set(this, coercedValue);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        });
    }

    private Object coerceType(Class<?> targetType, Object value) {
        if (value == null) return null;

        // Handle number conversions (e.g., Double from JSON -> float for engine)
        if (value instanceof Number) {
            Number num = (Number) value;
            if (targetType == float.class || targetType == Float.class) return num.floatValue();
            if (targetType == int.class || targetType == Integer.class) return num.intValue();
            if (targetType == double.class || targetType == Double.class) return num.doubleValue();
            if (targetType == long.class || targetType == Long.class) return num.longValue();
        }

        // Default fallback if types already match
        return value;
    }
}
