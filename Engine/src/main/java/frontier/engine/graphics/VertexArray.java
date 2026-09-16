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
                3,
                GL11.GL_FLOAT,
                false,
                5 * Float.BYTES,
                0
        );

        GL20.glEnableVertexAttribArray(0);
    }

    public void addVertexBuffer(VertexBuffer vertexBuffer) {
        bind();
        vertexBuffer.bind();

        GL20.glVertexAttribPointer(
                0,
                3,
                GL11.GL_FLOAT,
                false,
                8 * Float.BYTES,
                0
        );

        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(
                1,
                3,
                GL11.GL_FLOAT,
                false,
                8 * Float.BYTES,
                3 * Float.BYTES
        );

        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(
                2,
                2,
                GL11.GL_FLOAT,
                false,
                8 * Float.BYTES,
                6 * Float.BYTES
        );

        GL20.glEnableVertexAttribArray(2);
    }

    public void setIndexBuffer(IndexBuffer indexBuffer) {
        // this.indexBuffer = indexBuffer;
        indexBuffer.bind();
    }

    public void bind() {
        GL30.glBindVertexArray(vao);
    }

    public void delete() {
        GL30.glDeleteVertexArrays(vao);
    }
}
