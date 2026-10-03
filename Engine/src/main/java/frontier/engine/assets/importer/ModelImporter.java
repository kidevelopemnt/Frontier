package frontier.engine.assets.importer;

import frontier.engine.assets.AssetResource;
import frontier.engine.assets.Model;

import java.io.IOException;
import java.nio.file.Path;

public interface ModelImporter {
    Model loadModel(AssetResource resource) throws IOException;
}
