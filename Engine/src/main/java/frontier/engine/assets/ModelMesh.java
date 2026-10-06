package frontier.engine.assets;

public class ModelMesh {

    private final Mesh mesh;
    private Material material;

    public ModelMesh(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

}
