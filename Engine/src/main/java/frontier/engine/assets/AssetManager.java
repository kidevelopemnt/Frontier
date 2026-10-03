package frontier.engine.assets;

import frontier.engine.Engine;
import frontier.engine.assets.importer.ModelImporter;
import frontier.engine.assets.importer.ObjImporter;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private final Engine engine;

    private final Map<Path, Texture> textureMap = new HashMap<>();

    public AssetManager(Engine engine) {
        this.engine = engine;
    }

    public Model loadModel(String path) throws IOException {
        AssetResource resource = resolveResource(path);

        String extension = FilenameUtils.getExtension(path)
                .toLowerCase();

        ModelImporter importer = switch (extension) {
            case "obj" -> new ObjImporter();

            case "fbx" -> {
                engine.getLogger().logWarn(
                        "Loading FBX is not implemented... skipping model."
                );
                yield null;
            }

            default -> {
                engine.getLogger().logError(
                        "Invalid model file type: " + extension
                );
                yield null;
            }
        };

        if (importer == null) {
            return null;
        }

        Model model = importer.loadModel(resource);

        engine.getLogger().logDebug(
                "Imported model " + path
        );

        return model;
    }

    public Texture loadTexture(String path) {
        AssetResource resource = resolveResource(path);

        if (resource == null) {
            return null;
        }

        return new Texture(resource);
    }

    private AssetResource resolveResource(String path) {
        path = path.replace('\\', '/');

        Path projectResource = engine.getApp()
                .getProjectDirectory()
                .resolve("src/main/resources")
                .resolve(path);

        if (Files.exists(projectResource)) {
            return AssetResource.fromFile(path, projectResource);
        }

        if (AssetManager.class
                .getClassLoader()
                .getResource(path) != null) {

            return AssetResource.fromClasspath(
                    path,
                    AssetManager.class.getClassLoader()
            );
        }

        engine.getLogger().logError(
                "Resource not found: " + path
        );

        return null;
    }
}