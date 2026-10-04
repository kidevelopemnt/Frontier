package frontier.engine.assets.importer;

import frontier.engine.assets.*;
import frontier.engine.graphics.Shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class ObjImporter implements ModelImporter {

    private final AssetManager assetManager;
    private final Shader defaultShader;

    public ObjImporter(AssetManager assetManager, Shader defaultShader) {
        this.assetManager = assetManager;
        this.defaultShader = defaultShader;
    }

    @Override
    public Model loadModel(AssetID id, AssetResource resource)
            throws IOException {

        List<float[]> positions = new ArrayList<>();
        List<float[]> texCoords = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();

        Map<String, Material> materials = new HashMap<>();
        List<ModelMesh> modelMeshes = new ArrayList<>();

        MeshBuilder currentMesh = new MeshBuilder(null);
        List<String> materialLibraries = new ArrayList<>();

        try (var reader = new BufferedReader(
                new InputStreamReader(
                        resource.openStream(),
                        StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+", 2);
                String directive = parts[0];
                String value = parts.length > 1 ? parts[1].trim() : "";

                switch (directive) {
                    case "v" -> positions.add(parseVector(value, 3));
                    case "vt" -> texCoords.add(parseVector(value, 2));
                    case "vn" -> normals.add(parseVector(value, 3));

                    case "mtllib" -> {
                        for (String library : value.split("\\s+")) {
                            if (!library.isBlank()) {
                                materialLibraries.add(library);
                            }
                        }
                    }

                    case "usemtl" -> {
                        String materialName = value;

                        if (!Objects.equals(
                                currentMesh.materialName, materialName)) {

                            addMesh(currentMesh, modelMeshes, materials);
                            currentMesh = new MeshBuilder(materialName);
                        }
                    }

                    case "o", "g" -> {
                        addMesh(currentMesh, modelMeshes, materials);
                        currentMesh = new MeshBuilder(
                                currentMesh.materialName);
                    }

                    case "f" -> parseFace(
                            value.split("\\s+"),
                            positions,
                            texCoords,
                            normals,
                            currentMesh);
                }
            }
        }

        for (String library : materialLibraries) {
            loadMaterials(resource, library, materials);
        }

        addMesh(currentMesh, modelMeshes, materials);

        return new Model(id, modelMeshes);
    }

    private void loadMaterials(
            AssetResource objResource,
            String library,
            Map<String, Material> materials) throws IOException {

        AssetResource mtlResource = resolveRelative(objResource, library);

        try (var reader = new BufferedReader(
                new InputStreamReader(
                        mtlResource.openStream(),
                        StandardCharsets.UTF_8))) {

            String materialName = null;
            String diffuseTexture = null;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+", 2);
                String directive = parts[0];
                String value = parts.length > 1 ? parts[1].trim() : "";

                if (directive.equals("newmtl")) {
                    if (materialName != null) {
                        createMaterial(
                                objResource, mtlResource,
                                materialName, diffuseTexture, materials);
                    }

                    materialName = value;
                    diffuseTexture = null;
                } else if (directive.equals("map_Kd")) {
                    // Basic support: map_Kd followed by a texture path.
                    diffuseTexture = extractTexturePath(value);
                }
            }

            if (materialName != null) {
                createMaterial(
                        objResource, mtlResource,
                        materialName, diffuseTexture, materials);
            }
        }
    }

    private void createMaterial(
            AssetResource objResource,
            AssetResource mtlResource,
            String name,
            String texturePath,
            Map<String, Material> materials) throws IOException {

        Texture texture = null;

        if (texturePath != null && !texturePath.isBlank()) {
            AssetResource textureResource =
                    resolveRelative(mtlResource, texturePath);

            // Use the logical resource path as the asset identity.
            texture = assetManager.loadTexture(
                    textureResource.getPath());
        }

        Material material = new Material(
                null,
                defaultShader,
                texture);

        materials.put(name, material);
    }

    private String extractTexturePath(String value) {
        // Supports ordinary paths and quoted paths containing spaces.
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }

        if (value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1);
        }

        // MTL map options (such as -s and -o) need dedicated parsing.
        // For now, assume the path itself has no spaces or map options.
        String[] parts = value.split("\\s+");
        return parts[parts.length - 1];
    }

    private AssetResource resolveRelative(
            AssetResource base,
            String relativePath) throws IOException {

        String normalizedBase = base.getPath().replace('\\', '/');
        Path parent = Path.of(normalizedBase).getParent();

        Path resolved = (parent == null
                ? Path.of(relativePath)
                : parent.resolve(relativePath))
                .normalize();

        String path = resolved.toString().replace('\\', '/');

        if (base.isFile()) {
            Path file = base.getFile().getParent()
                    .resolve(relativePath).normalize();

            if (!java.nio.file.Files.isRegularFile(file)) {
                throw new IOException(
                        "Referenced asset not found: " + file);
            }

            return AssetResource.fromFile(path, file);
        }

        ClassLoader loader = ObjImporter.class.getClassLoader();

        if (loader.getResource(path) == null) {
            throw new IOException(
                    "Referenced classpath asset not found: " + path);
        }

        return AssetResource.fromClasspath(path, loader);
    }

    private void addMesh(
            MeshBuilder builder,
            List<ModelMesh> modelMeshes,
            Map<String, Material> materials) {

        if (builder.indices.isEmpty()) {
            return;
        }

        float[] vertices = new float[builder.vertices.size()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = builder.vertices.get(i);
        }

        int[] indices = new int[builder.indices.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = builder.indices.get(i);
        }

        Mesh mesh = new Mesh(vertices, indices);
        Material material = materials.get(builder.materialName);

        modelMeshes.add(new ModelMesh(mesh, material));
    }

    private void parseFace(
            String[] face,
            List<float[]> positions,
            List<float[]> texCoords,
            List<float[]> normals,
            MeshBuilder mesh) {

        List<Integer> faceIndices = new ArrayList<>();

        for (String item : face) {
            String[] data = item.split("/", -1);

            int positionIndex = parseIndex(data[0], positions.size());
            int texCoordIndex = data.length > 1 && !data[1].isEmpty()
                    ? parseIndex(data[1], texCoords.size()) : -1;
            int normalIndex = data.length > 2 && !data[2].isEmpty()
                    ? parseIndex(data[2], normals.size()) : -1;

            VertexKey key = new VertexKey(
                    positionIndex, texCoordIndex, normalIndex);

            Integer vertexIndex = mesh.vertexMap.get(key);

            if (vertexIndex == null) {
                vertexIndex = mesh.vertices.size() / 8;

                addVertex(
                        positions.get(positionIndex),
                        texCoordIndex >= 0 ? texCoords.get(texCoordIndex) : null,
                        normalIndex >= 0 ? normals.get(normalIndex) : null,
                        mesh.vertices);

                mesh.vertexMap.put(key, vertexIndex);
            }

            faceIndices.add(vertexIndex);
        }

        for (int i = 1; i < faceIndices.size() - 1; i++) {
            mesh.indices.add(faceIndices.get(0));
            mesh.indices.add(faceIndices.get(i));
            mesh.indices.add(faceIndices.get(i + 1));
        }
    }

    private void addVertex(
            float[] position,
            float[] texCoord,
            float[] normal,
            List<Float> vertices) {

        Collections.addAll(vertices,
                position[0], position[1], position[2]);

        if (normal != null) {
            Collections.addAll(vertices, normal[0], normal[1], normal[2]);
        } else {
            Collections.addAll(vertices, 0f, 0f, 0f);
        }

        if (texCoord != null) {
            Collections.addAll(vertices, texCoord[0], texCoord[1]);
        } else {
            Collections.addAll(vertices, 0f, 0f);
        }
    }

    private float[] parseVector(String value, int count) {
        String[] parts = value.split("\\s+");
        float[] result = new float[count];

        for (int i = 0; i < count; i++) {
            result[i] = Float.parseFloat(parts[i]);
        }

        return result;
    }

    private int parseIndex(String value, int size) {
        int index = Integer.parseInt(value);
        return index > 0 ? index - 1 : size + index;
    }

    private static class MeshBuilder {
        private final String materialName;
        private final List<Float> vertices = new ArrayList<>();
        private final List<Integer> indices = new ArrayList<>();
        private final Map<VertexKey, Integer> vertexMap = new HashMap<>();

        private MeshBuilder(String materialName) {
            this.materialName = materialName;
        }
    }

    private record VertexKey(
            int positionIndex,
            int texCoordIndex,
            int normalIndex) {
    }
}

