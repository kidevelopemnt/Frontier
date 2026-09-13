package frontier.engine.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

public class Renderer {
    private int vao;
    private int vbo;
    private int shaderProgram;

    private String loadShader(String path) {

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(path)) {

            if (input == null) {
                throw new RuntimeException(
                        "Shader not found: " + path
                );
            }

            return new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load shader: " + path,
                    e
            );
        }
    }

    public void initialize() {
        String vertexSource =
                loadShader("shaders/basic.vert");

        String fragmentSource =
                loadShader("shaders/basic.frag");

        float[] vertices = {
                0.2f,  0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f
        };

        FloatBuffer vertexBuffer =
                BufferUtils.createFloatBuffer(vertices.length);

        vertexBuffer.put(vertices);
        vertexBuffer.flip();

        // VBO
        vbo = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL15.glBufferData(
                GL15.GL_ARRAY_BUFFER,
                vertexBuffer,
                GL15.GL_STATIC_DRAW
        );

        // VAO
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

        // Shaders
        // Load vertexSource and fragmentSource here.

        int vertexShader =
                GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        GL20.glShaderSource(vertexShader, vertexSource);
        GL20.glCompileShader(vertexShader);

        if (GL20.glGetShaderi(
                vertexShader,
                GL20.GL_COMPILE_STATUS
        ) == GL11.GL_FALSE) {

            throw new RuntimeException(
                    GL20.glGetShaderInfoLog(vertexShader)
            );
        }

        int fragmentShader =
                GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(fragmentShader, fragmentSource);
        GL20.glCompileShader(fragmentShader);

        if (GL20.glGetShaderi(
                fragmentShader,
                GL20.GL_COMPILE_STATUS
        ) == GL11.GL_FALSE) {

            throw new RuntimeException(
                    GL20.glGetShaderInfoLog(fragmentShader)
            );
        }

        // Program
        shaderProgram = GL20.glCreateProgram();

        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);

        GL20.glLinkProgram(shaderProgram);

        if (GL20.glGetProgrami(
                shaderProgram,
                GL20.GL_LINK_STATUS
        ) == GL11.GL_FALSE) {

            throw new RuntimeException(
                    GL20.glGetProgramInfoLog(shaderProgram)
            );
        }

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    public void beginFrame() {
        clear();
    }

    public void render() {

        GL20.glUseProgram(shaderProgram);

        GL30.glBindVertexArray(vao);

        GL11.glDrawArrays(
                GL11.GL_TRIANGLES,
                0,
                3
        );
    }

    public void endFrame() {

    }

    public void shutdown() {

        GL20.glDeleteProgram(shaderProgram);
        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);
    }

    public void clear() {
        GL11.glClear(
                GL11.GL_COLOR_BUFFER_BIT |
                        GL11.GL_DEPTH_BUFFER_BIT
        );
    }

    public void setFillColor(
            float r,
            float g,
            float b,
            float a
    ) {
        GL11.glClearColor(r, g, b, a);
    }
}
