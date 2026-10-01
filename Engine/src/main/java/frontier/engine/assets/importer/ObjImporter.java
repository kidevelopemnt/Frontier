package frontier.engine.assets.importer;

import frontier.engine.assets.Mesh;
import frontier.engine.assets.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjImporter implements ModelImporter {
    @Override
    public Model loadModel(Path path) throws IOException {

        List<float[]> positions = new ArrayList<>();
        List<float[]> texCoords = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();

        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Map<VertexKey, Integer> vertexMap = new HashMap<>();

        try (var lines = Files.lines(path)) {

            for (String line : (Iterable<String>) lines::iterator) {

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
                            vertices,
                            indices,
                            vertexMap
                    );
                }
            }
        }

        float[] vertexArray = new float[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }

        int[] indexArray = new int[indices.size()];

        for (int i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }

        Mesh mesh = new Mesh(vertexArray, indexArray);

        return new Model(List.of(mesh));
    }

    private void parseFace(
            String[] parts,
            List<float[]> positions,
            List<float[]> texCoords,
            List<float[]> normals,
            List<Float> vertices,
            List<Integer> indices,
            Map<VertexKey, Integer> vertexMap) {

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

            Integer vertexIndex = vertexMap.get(key);

            if (vertexIndex == null) {

                vertexIndex = vertices.size() / 8;

                addVertex(
                        positions.get(positionIndex),
                        texCoordIndex >= 0 ? texCoords.get(texCoordIndex) : null,
                        normalIndex >= 0 ? normals.get(normalIndex) : null,
                        vertices
                );

                vertexMap.put(key, vertexIndex);
            }

            faceIndices.add(vertexIndex);
        }

        // Triangulate the face.
        for (int i = 1; i < faceIndices.size() - 1; i++) {

            indices.add(faceIndices.get(0));
            indices.add(faceIndices.get(i));
            indices.add(faceIndices.get(i + 1));
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
}
