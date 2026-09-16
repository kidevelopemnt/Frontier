package frontier.game;

import frontier.engine.Engine;
import frontier.engine.GameObject;
import frontier.engine.core.Logger;
import frontier.engine.game.IGame;
import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;
import frontier.engine.graphics.Shader;
import frontier.engine.graphics.Texture;
import frontier.engine.graphics.lighting.DirectionalLight;
import org.joml.Vector3f;
import org.w3c.dom.Text;

import java.io.InputStream;

public class FrontierGame implements IGame {

    private Engine engine;

    private GameObject cube;

    @Override
    public void initialize(Engine engine) {
        this.engine = engine;
        engine.getRenderer().setFillColor(.1f, .2f, .1f, 1.0f);

        float[] vertices = {
                // Front (+Z)
                // position             normal          UV
                -0.5f, -0.5f,  0.5f,    0, 0, 1,       0, 0,
                0.5f, -0.5f,  0.5f,    0, 0, 1,       1, 0,
                0.5f,  0.5f,  0.5f,    0, 0, 1,       1, 1,
                -0.5f,  0.5f,  0.5f,    0, 0, 1,       0, 1,

                // Back (-Z)
                -0.5f, -0.5f, -0.5f,    0, 0, -1,      1, 0,
                0.5f, -0.5f, -0.5f,    0, 0, -1,      0, 0,
                0.5f,  0.5f, -0.5f,    0, 0, -1,      0, 1,
                -0.5f,  0.5f, -0.5f,    0, 0, -1,      1, 1,

                // Left (-X)
                -0.5f, -0.5f, -0.5f,   -1, 0, 0,       0, 0,
                -0.5f, -0.5f,  0.5f,   -1, 0, 0,       1, 0,
                -0.5f,  0.5f,  0.5f,   -1, 0, 0,       1, 1,
                -0.5f,  0.5f, -0.5f,   -1, 0, 0,       0, 1,

                // Right (+X)
                0.5f, -0.5f,  0.5f,    1, 0, 0,       0, 0,
                0.5f, -0.5f, -0.5f,    1, 0, 0,       1, 0,
                0.5f,  0.5f, -0.5f,    1, 0, 0,       1, 1,
                0.5f,  0.5f,  0.5f,    1, 0, 0,       0, 1,

                // Top (+Y)
                -0.5f,  0.5f,  0.5f,    0, 1, 0,       0, 0,
                0.5f,  0.5f,  0.5f,    0, 1, 0,       1, 0,
                0.5f,  0.5f, -0.5f,    0, 1, 0,       1, 1,
                -0.5f,  0.5f, -0.5f,    0, 1, 0,       0, 1,

                // Bottom (-Y)
                -0.5f, -0.5f, -0.5f,    0, -1, 0,      0, 1,
                0.5f, -0.5f, -0.5f,    0, -1, 0,      1, 1,
                0.5f, -0.5f,  0.5f,    0, -1, 0,      1, 0,
                -0.5f, -0.5f,  0.5f,    0, -1, 0,      0, 0
        };

        int[] indices = {
                0, 1, 2,    0, 2, 3,
                4, 5, 6,    4, 6, 7,
                8, 9, 10,   8, 10, 11,
                12, 13, 14, 12, 14, 15,
                16, 17, 18, 16, 18, 19,
                20, 21, 22, 20, 22, 23
        };

        Mesh cubeMesh = new Mesh(vertices, indices);

        Texture cubeTexture = new Texture(
                "textures/crate.jpg"
        );

        Material cubeMaterial = new Material(
                engine.getRenderer().getDefaultShader(),
                cubeTexture
        );

        cube = new GameObject(
                cubeMesh,
                cubeMaterial
        );

        cube.getTransform().rotation.y = (float) Math.toRadians(30);
        cube.getTransform().rotation.x = (float) Math.toRadians(20);

        DirectionalLight sun = new DirectionalLight(
                new Vector3f(-1, -1, -1),
                new Vector3f(1, 1, 1),
                1.0f
        );

        engine.object = cube;
        engine.lightObject = sun;
    }

    @Override
    public void update(float deltaTime) {
        cube.getTransform().rotation.y += 0.25f;
    }

    @Override
    public void shutdown() {

    }
}
