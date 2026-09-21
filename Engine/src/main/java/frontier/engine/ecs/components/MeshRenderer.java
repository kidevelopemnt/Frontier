package frontier.engine.ecs.components;

import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;

import java.util.HashMap;
import java.util.Map;

public class MeshRenderer extends Component {
    private Mesh mesh;
    private Material material;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        // TODO: Save mesh renderer

        return data;
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
