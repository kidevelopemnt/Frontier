package frontier.engine.ecs.components;

import frontier.engine.assets.Material;
import frontier.engine.assets.Mesh;
import frontier.engine.assets.Model;
import frontier.engine.assets.ModelMesh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeshRenderer extends Component {
    private Model model;

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

    public void setModel(Model model) {
        this.model = model;
    }

    public void setMaterial(Material material, List<ModelMesh> meshes) {
        for (ModelMesh mm : meshes) {
            mm.setMaterial(material);
        }
    }

    public void setMaterial(Material material) {
        setMaterial(material, model.getMeshes());
    }

    public Model getModel() {
        return model;
    }
}
