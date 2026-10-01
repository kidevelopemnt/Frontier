package frontier.engine.assets;

import frontier.engine.Engine;
import frontier.engine.assets.importer.ModelImporter;
import frontier.engine.assets.importer.ObjImporter;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Path;

public class AssetManager {
    private Engine engine;

    public AssetManager(Engine engine) {
        this.engine = engine;
    }

    public Model loadModel(Path path) throws IOException {
        path = engine.getApp().getProjectDirectory().resolve("src/main/resources/").resolve(path);
        String extension = FilenameUtils.getExtension(path.toString());

        ModelImporter importer;

        switch (extension) {
            case "obj": {
                importer = new ObjImporter();
                break;
            }
            case "fbx": {
                engine.getLogger().logWarn("Load FBX is not implemented... skipping model.");
                return null;
            }
            default: {
                engine.getLogger().logError("Invalid model file type " + extension);
                return null;
            }
        }

        Model model = importer.loadModel(path);
        engine.getLogger().logDebug("Imported model " + path);
        return model;
    }
}
