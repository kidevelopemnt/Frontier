package frontier.engine.ecs.components;

import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MeshRenderer extends Component {
    private Mesh mesh;
    private Material material;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("mesh", mesh.getName());
        data.put("material", material.getName());

        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        mesh = Mesh.registry.get(data.get("mesh"));  // TODO: Replace with ResourceManager
        material = Material.registry.get(data.get("material"));
    }

    public Material getMaterial() {
        return material;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
