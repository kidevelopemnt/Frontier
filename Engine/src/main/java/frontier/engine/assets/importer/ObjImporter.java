package frontier.engine.assets.importer;

import frontier.engine.assets.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjImporter implements ModelImporter {

    @Override
    public Model loadModel(AssetID id, AssetResource resource) throws IOException {

        List<float[]> positions = new ArrayList<>();
        List<float[]> texCoords = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();

        List<ModelMesh> modelMeshes = new ArrayList<>();

        MeshBuilder currentMesh = new MeshBuilder();

        try (var lines = new BufferedReader(
                new InputStreamReader(resource.openStream()))) {

            for (String line : lines.readAllLines()) {

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+");

                switch (parts[0]) {

                    case "v" -> positions.add(new float[]{
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])
                    });

                    case "vt" -> texCoords.add(new float[]{
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2])
                    });

                    case "vn" -> normals.add(new float[]{
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])
                    });

                    case "f" -> parseFace(
                            parts,
                            positions,
                            texCoords,
                            normals,
                            currentMesh
                    );

                    case "o", "g" -> {

                        // Finish the current mesh.
                        addMesh(currentMesh, modelMeshes);

                        // Start a new mesh.
                        currentMesh = new MeshBuilder();
                    }
                }
            }
        }

        // Finish the final mesh.
        addMesh(currentMesh, modelMeshes);

        return new Model(
                id,
                modelMeshes
        );
    }

    private void addMesh(
            MeshBuilder builder,
            List<ModelMesh> modelMeshes) {

        if (builder.indices.isEmpty()) {
            return;
        }

        float[] vertexArray = new float[builder.vertices.size()];

        for (int i = 0; i < builder.vertices.size(); i++) {
            vertexArray[i] = builder.vertices.get(i);
        }

        int[] indexArray = new int[builder.indices.size()];

        for (int i = 0; i < builder.indices.size(); i++) {
            indexArray[i] = builder.indices.get(i);
        }

        Mesh mesh = new Mesh(
                vertexArray,
                indexArray
        );

        ModelMesh modelMesh = new ModelMesh(
                mesh,
                null
        );

        modelMeshes.add(modelMesh);
    }

    private void parseFace(
            String[] parts,
            List<float[]> positions,
            List<float[]> texCoords,
            List<float[]> normals,
            MeshBuilder mesh) {

        List<Integer> faceIndices = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {

            String[] vertexData = parts[i].split("/");

            int positionIndex = parseIndex(
                    vertexData[0],
                    positions.size()
            );

            int texCoordIndex = -1;
            int normalIndex = -1;

            if (vertexData.length > 1 && !vertexData[1].isEmpty()) {
                texCoordIndex = parseIndex(
                        vertexData[1],
                        texCoords.size()
                );
            }

            if (vertexData.length > 2 && !vertexData[2].isEmpty()) {
                normalIndex = parseIndex(
                        vertexData[2],
                        normals.size()
                );
            }

            VertexKey key = new VertexKey(
                    positionIndex,
                    texCoordIndex,
                    normalIndex
            );

            Integer vertexIndex = mesh.vertexMap.get(key);

            if (vertexIndex == null) {

                vertexIndex = mesh.vertices.size() / 8;

                addVertex(
                        positions.get(positionIndex),
                        texCoordIndex >= 0
                                ? texCoords.get(texCoordIndex)
                                : null,
                        normalIndex >= 0
                                ? normals.get(normalIndex)
                                : null,
                        mesh.vertices
                );

                mesh.vertexMap.put(key, vertexIndex);
            }

            faceIndices.add(vertexIndex);
        }

        // Triangulate the face.
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

        // Position
        vertices.add(position[0]);
        vertices.add(position[1]);
        vertices.add(position[2]);

        // Normal
        if (normal != null) {
            vertices.add(normal[0]);
            vertices.add(normal[1]);
            vertices.add(normal[2]);
        } else {
            vertices.add(0.0f);
            vertices.add(0.0f);
            vertices.add(0.0f);
        }

        // UV
        if (texCoord != null) {
            vertices.add(texCoord[0]);
            vertices.add(texCoord[1]);
        } else {
            vertices.add(0.0f);
            vertices.add(0.0f);
        }
    }

    private int parseIndex(String value, int size) {

        int index = Integer.parseInt(value);

        // OBJ indices are 1-based.
        if (index > 0) {
            return index - 1;
        }

        // OBJ supports negative indices.
        return size + index;
    }

    private static class MeshBuilder {

        private final List<Float> vertices = new ArrayList<>();
        private final List<Integer> indices = new ArrayList<>();
        private final Map<VertexKey, Integer> vertexMap = new HashMap<>();
    }
}