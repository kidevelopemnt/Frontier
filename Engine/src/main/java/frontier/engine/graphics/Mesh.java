package frontier.engine.graphics;

public class Mesh {
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
}
