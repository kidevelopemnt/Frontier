package frontier.engine.ecs.components;

import frontier.engine.assets.Material;
import frontier.engine.assets.Mesh;

import java.util.HashMap;
import java.util.Map;

public class MeshRenderer extends Component {
    private Mesh mesh;
    private Material material;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        // data.put("mesh", mesh.getName());
        // data.put("material", material.getName());

        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        // TODO: Revist loading with AssetManager
        // mesh = Mesh.registry.get(data.get("mesh"));
        // material = Material.registry.get(data.get("material"));
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
