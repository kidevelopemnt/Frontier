package frontier.engine.assets;

import frontier.engine.graphics.Shader;
import org.lwjgl.opengl.GL13;

import java.util.HashMap;
import java.util.Map;

public class Material implements Asset {
    private Shader shader;
    private Texture texture;

    public Material (Shader shader, Texture texture) {
        this.shader = shader;
        this.texture = texture;
    }

    public Material (Texture texture) {
        this.texture = texture;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
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
