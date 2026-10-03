package frontier.engine.assets.importer;

import frontier.engine.assets.AssetID;
import frontier.engine.assets.AssetResource;
import frontier.engine.assets.Model;

import java.io.IOException;
import java.nio.file.Path;

public interface ModelImporter {
    Model loadModel(AssetID id, AssetResource resource) throws IOException;
}
