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

    private final Map<AssetID, Asset> assets = new HashMap<>();

    public AssetManager(Engine engine) {
        this.engine = engine;
    }

    public Model loadModel(String path) throws IOException {
        AssetID id = new AssetID(path);

        Model cached = getCached(id, Model.class);

        if (cached != null) {
            return cached;
        }

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

        Model model = importer.loadModel(id, resource);
        assets.put(id, model);

        engine.getLogger().logDebug(
                "Imported model " + path
        );

        return model;
    }

    public Texture loadTexture(String path) {
        AssetID id = new AssetID(path);
        Texture cached = getCached(id, Texture.class);

        if (cached != null) {
            System.out.println("CACHED");
            return cached;
        }

        AssetResource resource = resolveResource(path);

        if (resource == null) {
            return null;
        }

        Texture texture = new Texture(id, resource);
        assets.put(id, texture);

        return texture;
    }

    public <T extends Asset> T get(AssetID id, Class<T> type) {
        return getCached(id, type);
    }

    public <T extends Asset> T get(String id, Class<T> type) {
        return get(new AssetID(id), type);
    }

    public boolean unload(AssetID id) {
        Asset asset = assets.remove(id);

        if (asset == null) {
            return false;
        }

        asset.unload();

        engine.getLogger().logDebug(
                "Unloaded asset " + id
        );

        return true;
    }

    public boolean unload(String id) {
        return unload(new AssetID(id));
    }

    public void unloadAll() {
        for (Asset asset : assets.values()) {
            asset.unload();
        }

        assets.clear();

        engine.getLogger().logDebug("Unloaded all assets.");
    }

    @SuppressWarnings("unchecked")
    private <T extends Asset> T getCached(AssetID id, Class<T> type) {
        Asset asset = assets.get(id);

        if (asset == null) {
            return null;
        }

        if (!type.isInstance(asset)) {
            throw new IllegalStateException(
                    "Asset ID " + id + " is already registered as "
                            + asset.getClass().getSimpleName()
                            + ", not "
                            + type.getSimpleName()
            );
        }

        return (T) asset;
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