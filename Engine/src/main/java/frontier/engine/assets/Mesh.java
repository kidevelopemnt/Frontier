package frontier.engine.assets;

import frontier.engine.graphics.IndexBuffer;
import frontier.engine.graphics.VertexArray;
import frontier.engine.graphics.VertexBuffer;

import java.util.HashMap;
import java.util.Map;

public class Mesh extends Asset {
    private float[] vertices;
    private int[] indices;

    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;
    private VertexArray vertexArray;

    public Mesh(AssetID id, float[] vertices, int[] indices) {
        super(id);
        this.vertices = vertices;
        this.indices = indices;

        vertexArray = new VertexArray();

        vertexBuffer = new VertexBuffer(vertices);
        vertexArray.addVertexBuffer(vertexBuffer);

        indexBuffer = new IndexBuffer(indices);
        vertexArray.setIndexBuffer(indexBuffer);
    }

    public Mesh(float[] vertices, int[] indices) {
        this(null, vertices, indices);
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

    public Mesh copy() {
        return new Mesh(vertices, indices);
    }
}
