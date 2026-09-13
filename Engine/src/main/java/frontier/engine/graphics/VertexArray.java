package frontier.engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VertexArray {
    private int vao;

    public VertexArray() {
        vao = GL30.glGenVertexArrays();

        GL30.glBindVertexArray(vao);

        GL20.glVertexAttribPointer(
                0,
                2,
                GL11.GL_FLOAT,
                false,
                2 * Float.BYTES,
                0
        );

        GL20.glEnableVertexAttribArray(0);
    }

    public void addVertexBuffer(VertexBuffer vertexBuffer) {
        bind();
        vertexBuffer.bind();

        GL20.glVertexAttribPointer(
                0,
                2,
                GL11.GL_FLOAT,
                false,
                2 * Float.BYTES,
                0
        );

        GL20.glEnableVertexAttribArray(0);
    }

    public void bind() {
        GL30.glBindVertexArray(vao);
    }

    public void delete() {
        GL30.glDeleteVertexArrays(vao);
    }
}
