package frontier.engine;

// TODO: Move somewhere else

import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;
import frontier.engine.graphics.Transform;

public class GameObject {
    private Transform transform;
    private Mesh mesh;
    private Material material;

    public GameObject (Mesh mesh, Material material) {
        this.mesh = mesh;
        this.transform = new Transform();
        this.material = material;
    }

    public Transform getTransform() {
        return transform;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Material getMaterial() {
        return material;
    }

    public void delete() {
        mesh.delete();
    }
}
