package frontier.engine.assets;

import frontier.engine.graphics.Shader;
import org.lwjgl.opengl.GL13;

import java.util.HashMap;
import java.util.Map;

public class Material extends Asset {
    private Shader shader;
    private Texture texture;

    public Material(AssetID id, Shader shader, Texture texture) {
        super(id);
        this.shader = shader;
        this.texture = texture;
    }

    public Material (Shader shader, Texture texture) {
        this(null, shader, texture);
    }

    public Material (Texture texture) {
        this(null, null, texture);
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

    @Override
    public void unload() {
        texture.unload();
    }
}
