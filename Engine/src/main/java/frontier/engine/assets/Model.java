package frontier.engine.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model extends Asset {
    private final List<Mesh> meshes;

    public Model(AssetID id, List<Mesh> meshes) {
        super(id);
        this.meshes = new ArrayList<>(meshes);
    }

    public Model(List<Mesh> meshes) {
        this(null, meshes);
    }

    public List<Mesh> getMeshes() {
        return Collections.unmodifiableList(meshes);
    }
}
