package frontier.engine.assets;

import frontier.engine.graphics.IndexBuffer;
import frontier.engine.graphics.VertexArray;
import frontier.engine.graphics.VertexBuffer;

import java.util.HashMap;
import java.util.Map;

public class Mesh implements Asset {
    private String name; // TODO: This is temporary, will replace with resource manager
    public static Map<String, Mesh> registry = new HashMap<>();

    private float[] vertices;
    private int[] indices;

    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;
    private VertexArray vertexArray;

    public Mesh(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;

        vertexArray = new VertexArray();

        vertexBuffer = new VertexBuffer(vertices);
        vertexArray.addVertexBuffer(vertexBuffer);

        indexBuffer = new IndexBuffer(indices);
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

    public Mesh copy() {
        return new Mesh(vertices, indices);
    }
}
