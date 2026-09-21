package frontier.engine.graphics;

import org.lwjgl.opengl.GL13;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Material {
    private String name; // TODO: This is temporary, will replace with resource manager
    public static Map<String, Material> registry = new HashMap<>();

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

    public void setName(String name) {
        if (this.name != null) {
            registry.remove(this.name);
        }
        this.name = name;
        registry.put(this.name, this);
    }
    public String getName() {
        return name;
    }
}
