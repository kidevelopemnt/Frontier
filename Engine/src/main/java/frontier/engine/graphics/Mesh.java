package frontier.engine.graphics;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Mesh {
    private String name; // TODO: This is temporary, will replace with resource manager
    public static Map<String, Mesh> registry = new HashMap<>();

    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;
    private VertexArray vertexArray;

    public Mesh(float[] vertices, int[] indices) {
        vertexBuffer = new VertexBuffer(vertices);
        indexBuffer = new IndexBuffer(indices);

        vertexArray = new VertexArray();
        vertexArray.addVertexBuffer(vertexBuffer);
        vertexArray.setIndexBuffer(indexBuffer);
    }

    public void bind() {
        vertexArray.bind();
    }

    public int getIndexCount() {
        return indexBuffer.getCount();
    }

    public void delete() {
        vertexArray.delete();
        vertexBuffer.delete();
        indexBuffer.delete();
    }

    public void setName(String name) {
        if (this.name != null) {
            registry.remove(this.name);
        }
        this.name = name;
        registry.put(this.name, this);
    }
    public String getName() {
        return name;
    }

}
