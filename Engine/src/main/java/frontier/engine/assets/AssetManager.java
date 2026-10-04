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
import java.util.UUID;

public class AssetManager {

    private final Engine engine;

    private final Map<AssetID, Asset> assetById = new HashMap<>();
    private final Map<String, AssetID> idByPath = new HashMap<>();

    public AssetManager(Engine engine) {
        this.engine = engine;
    }

    public Model loadModel(String path) throws IOException {
        Model cached = getCached(path, Model.class);

        if (cached != null) {
            return cached;
        }

        AssetResource resource = resolveResource(path);

        String extension = FilenameUtils.getExtension(path)
                .toLowerCase();

        ModelImporter importer = switch (extension) {
            case "obj" -> new ObjImporter(this, engine.getRenderer().getDefaultShader());

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

        AssetID id = new AssetID();
        Model model = importer.loadModel(id, resource);
        assetById.put(id, model);
        idByPath.put(path, id);

        engine.getLogger().logDebug(
                "Imported model " + path
        );

        return model;
    }

    public Texture loadTexture(String path) {
        Texture cached = getCached(path, Texture.class);

        if (cached != null) {
            return cached;
        }

        AssetResource resource = resolveResource(path);

        if (resource == null) {
            return null;
        }

        AssetID id = new AssetID();
        Texture texture = new Texture(id, resource);
        assetById.put(id, texture);
        idByPath.put(path, id);

        return texture;
    }

    public <T extends Asset> T get(AssetID id, Class<T> type) {
        return getCached(id, type);
    }

    public <T extends Asset> T get(String path, Class<T> type) {
        return get(path, type);
    }

    public boolean unload(AssetID id) {
        Asset asset = assetById.remove(id);

        if (asset == null) {
            return false;
        }

        asset.unload();

        engine.getLogger().logDebug(
                "Unloaded asset " + id
        );

        return true;
    }

    public boolean unload(String path) {
        AssetID id = idByPath.get(path);
        idByPath.remove(path);
        return unload(id);
    }

    public void unloadAll() {
        for (Asset asset : assetById.values()) {
            asset.unload();
        }

        assetById.clear();
        idByPath.clear();

        engine.getLogger().logDebug("Unloaded all assets.");
    }

    @SuppressWarnings("unchecked")
    private <T extends Asset> T getCached(AssetID id, Class<T> type) {
        Asset asset = assetById.get(id);

        if (asset == null) {
            return null;
        }

        if (!type.isInstance(asset)) {
            throw new IllegalStateException(
                    "Asset ID " + id.toString() + " is already registered as "
                            + asset.getClass().getSimpleName()
                            + ", not "
                            + type.getSimpleName()
            );
        }

        return (T) asset;
    }

    private <T extends Asset> T getCached(String path, Class<T> type) {
        AssetID id = idByPath.get(path);
        return getCached(id, type);
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