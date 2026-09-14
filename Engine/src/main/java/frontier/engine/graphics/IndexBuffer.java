package frontier.engine.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;

public class IndexBuffer {
    private int id;
    private int count;

    public IndexBuffer(int[] indices) {
        count = indices.length;

        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip();

        id = GL15.glGenBuffers();

        GL15.glBindBuffer(
            GL15.GL_ELEMENT_ARRAY_BUFFER,
            id
        );

        GL15.glBufferData(
            GL15.GL_ELEMENT_ARRAY_BUFFER,
            buffer,
            GL15.GL_STATIC_DRAW
        );
    }

    public void bind() {
        GL15.glBindBuffer(
            GL15.GL_ELEMENT_ARRAY_BUFFER,
            id
        );
    }

    public int getCount() {
        return count;
    }

    public void delete() {
        GL15.glDeleteBuffers(id);
    }
}
