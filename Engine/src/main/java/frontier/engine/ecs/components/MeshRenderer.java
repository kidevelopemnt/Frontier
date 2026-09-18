package frontier.engine.ecs.components;

import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;

public class MeshRenderer extends Component {
    private Mesh mesh;
    private Material material;

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
