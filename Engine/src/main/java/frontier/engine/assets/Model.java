package frontier.engine.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model extends Asset {
    private final List<ModelMesh> meshes;

    public Model(AssetID id, List<ModelMesh> meshes) {
        super(id);
        this.meshes = new ArrayList<>(meshes);
    }

    public Model(List<ModelMesh> meshes) {
        this(null, meshes);
    }

    public Model(AssetID id, Mesh mesh, Material material) {
        this(id, List.of(new ModelMesh(mesh, material)));
    }

    public Model(Mesh mesh, Material material) {
        this(null, mesh, material);
    }

    public List<ModelMesh> getMeshes() {
        return Collections.unmodifiableList(meshes);
    }
}
