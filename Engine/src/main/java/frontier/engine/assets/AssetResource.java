package frontier.engine.assets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AssetResource {
    private final String path;
    private final Path file;
    private final ClassLoader classLoader;

    private AssetResource(String path, Path file, ClassLoader classLoader) {
        this.path = path;
        this.file = file;
        this.classLoader = classLoader;
    }

    public static AssetResource fromFile(String path, Path file) {
        return new AssetResource(path, file, null);
    }

    public static AssetResource fromClasspath(String path, ClassLoader classLoader) {
        return new AssetResource(path, null, classLoader);
    }

    public InputStream openStream() throws IOException {
        if (file != null) {
            return Files.newInputStream(file);
        }

        InputStream stream = classLoader.getResourceAsStream(path);

        if (stream == null) {
            throw new IOException("Resource not found: " + path);
        }

        return stream;
    }

    public String getPath() {
        return path;
    }

    public Path getFile() {
        return file;
    }

    public boolean isFile() {
        return file != null;
    }
}