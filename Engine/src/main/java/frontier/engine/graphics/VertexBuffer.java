package frontier.engine.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;

public class VertexBuffer {
    private float[] vertices;
    private FloatBuffer buffer;
    private int vbo;

    public VertexBuffer(float[] vertices) {
        this.vertices = vertices;

        buffer = BufferUtils.createFloatBuffer(vertices.length);

        buffer.put(vertices);
        buffer.flip();
    }

    public void bind() {
        vbo = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL15.glBufferData(
            GL15.GL_ARRAY_BUFFER,
                buffer,
            GL15.GL_STATIC_DRAW
        );
    }

    public void delete() {
        GL15.glDeleteBuffers(vbo);
    }
}


