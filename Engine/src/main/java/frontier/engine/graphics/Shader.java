package frontier.engine.graphics;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;


public class Shader {
    private String vertSource;
    private String fragSource;
    private int shaderProgram;

    public Shader(String vertPath, String fragPath) {
        vertSource = loadShader(vertPath);

        fragSource = loadShader(fragPath);

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        GL20.glShaderSource(vertexShader, vertSource);
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

        GL20.glShaderSource(fragmentShader, fragSource);
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

    public void bind() {
        GL20.glUseProgram(shaderProgram);
    }

    public void delete() {
        GL20.glDeleteProgram(shaderProgram);
    }

    public void setMatrix4f(String name, Matrix4f matrix) {
        int location = GL20.glGetUniformLocation(shaderProgram, name);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);

            matrix.get(buffer);

            GL20.glUniformMatrix4fv(
                    location,
                    false,
                    buffer
            );
        }
    }

    public void setMatrix3f(String name, Matrix3f matrix) {
        int location = GL20.glGetUniformLocation(shaderProgram, name);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);

            matrix.get(buffer);

            GL20.glUniformMatrix3fv(
                    location,
                    false,
                    buffer
            );
        }
    }

    public void setVector3f(String name, Vector3f value) {
        int location = GL20.glGetUniformLocation(shaderProgram, name);
        GL20.glUniform3f(location, value.x, value.y, value.z);
    }

    public void setInt(String name, int value) {
        int location = GL20.glGetUniformLocation(shaderProgram, name);
        GL20.glUniform1i(location, value);
    }

    public void setFloat(String name, float value) {
        int location = GL20.glGetUniformLocation(shaderProgram, name);
        GL20.glUniform1f(location, value);
    }


    private static String loadShader(String path) {

        try (InputStream input = Shader.class
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
}
