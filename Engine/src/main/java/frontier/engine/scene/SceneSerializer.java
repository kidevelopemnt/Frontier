package frontier.engine.scene;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

public class SceneSerializer {
    Engine engine;
    ObjectMapper mapper = new ObjectMapper();

    public SceneSerializer(Engine engine) {
        this.engine = engine;
    }

    public void save(Scene scene) {
        try {
            Path file = engine.getApp().getProjectDirectory().resolve("src/main/resources/scenes").resolve(scene.getFilepath());
            File fileObj = file.toFile();
            File parent = fileObj.getParentFile();

            // Create if not exists
            if (!parent.exists()) {
                parent.mkdirs();
            }
            fileObj.createNewFile();

            mapper.writeValue(fileObj, scene.serialize());
            engine.getLogger().logDebug("Saved scene to " + scene.getFilepath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(Scene scene, Path path) {
        try {
            // Use Jackson's built-in TypeReference map parsing
            Map<String, Object> data = mapper.readValue(path.toFile(), new TypeReference<Map<String, Object>>() {});
            System.out.println(data);

            String sceneName = (String) data.getOrDefault("name", "Unknown Scene");
            List<Map<String, Object>> entitiesData = (List<Map<String, Object>>) data.get("entities");

            if (entitiesData == null) return;

            for (Map<String, Object> entityData : entitiesData) {
                loadEntity(scene, entityData);
            }

        } catch (IOException e) {
            // Consider using a logger instead of printStackTrace
            e.printStackTrace();
        }
    }

    private void loadEntity(Scene scene, Map<String, Object> entityData) {
        String name = (String) entityData.getOrDefault("name", "Unnamed Entity");
        Entity entity = new Entity(name, scene);

        // Jackson parses JSON strings as Strings, not UUID objects.
        // This fixes the ClassCastException you would get from (UUID) entityData.get("id")
        if (entityData.containsKey("id")) {
            entity.setID(UUID.fromString(entityData.get("id").toString()));
        }

        scene.addEntity(entity);

        // Get all components (key starts with "__")
        entityData.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("__"))
                .filter(entry -> entry.getValue() instanceof Map)
                .forEach(entry -> {
                    String componentName = entry.getKey().substring(2); // Strip the "__" prefix
                    Map<?, ?> componentValue = (Map<?, ?>) entry.getValue();
                    loadComponent(entity, componentName, componentValue);
                });
    }

    private void loadComponent(Entity entity, String componentName, Map<?, ?> componentData) {
        System.out.println(componentName);
        try {
            Class<?> componentClass = Class.forName("frontier.engine.ecs.components." + componentName);
            Component component = entity.addComponent(componentClass);

            // Safely convert the map using Jackson's built-in converter
            Map<String, Object> typedComponentData = mapper.convertValue(componentData, new TypeReference<>() {});
            component.load(typedComponentData);
        } catch (ClassNotFoundException e) {
            System.err.println("Component class not found: " + componentName);
        }
    }
}
