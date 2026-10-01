package frontier.engine.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model implements Asset {
    private final List<Mesh> meshes;

    public Model(List<Mesh> meshes) {
        this.meshes = new ArrayList<>(meshes);
    }

    public List<Mesh> getMeshes() {
        return Collections.unmodifiableList(meshes);
    }
}
