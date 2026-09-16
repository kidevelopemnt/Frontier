package frontier.engine.graphics;

import org.lwjgl.opengl.GL13;

public class Material {
    private Shader shader;
    private Texture texture;

    public Material (Shader shader, Texture texture) {
        this.shader = shader;
        this.texture = texture;
    }

    public Shader getShader() {
        return shader;
    }

    public Texture getTexture() {
        return texture;
    }

    public void bind() {
        shader.bind();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        texture.bind();

        shader.setInt("textureSampler", 0);
    }

    public void delete() {
        texture.delete();
    }
}
