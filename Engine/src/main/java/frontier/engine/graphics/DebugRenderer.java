package frontier.engine.graphics;

import frontier.engine.ecs.components.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11C.glLineWidth;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class DebugRenderer {
    private static final Shader shader = new Shader("shaders/line.vert", "shaders/line.frag");
    private static final VertexArray vao = new VertexArray();
    private static VertexBuffer vbo;

    private static class DebugLine {
        Vector3f start;
        Vector3f end;
        Vector3f color;
        float width;

        DebugLine(Vector3f start, Vector3f end, Vector3f color, float width) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.width = width;
        }
    }

    private static final List<DebugLine> lines = new ArrayList<>();

    public static void drawLine(Vector3f start, Vector3f end, Vector3f color, float width) {
        lines.add(new DebugLine(
                new Vector3f(start),
                new Vector3f(end),
                new Vector3f(color),
                width
        ));
    }

    public static void drawRay(Vector3f origin, Vector3f direction, float distance) {
        drawLine(
                origin,
                new Vector3f(direction).mul(distance).add(origin),
                new Vector3f(1, 0, 0),
                5
        );
    }

    public static void render(Camera camera, float aspectRatio) {
        for (DebugLine line : lines) {
            renderLine(line.start, line.end, line.color, line.width, camera.getViewMatrix(), camera.getProjectionMatrix(aspectRatio));
        }
    }

    private static void renderLine(
            Vector3f start,
            Vector3f end,
            Vector3f color,
            float width,
            Matrix4f view,
            Matrix4f projection
    ) {
        shader.bind();

        shader.setMatrix4f("view", view);
        shader.setMatrix4f("projection", projection);
        shader.setVector3f("color", color);

        glLineWidth(width);

        float[] vertices = {
                start.x, start.y, start.z,
                end.x,   end.y,   end.z
        };

        VertexBuffer vbo = new VertexBuffer(vertices);
        vao.addPositionBuffer(vbo);
        vao.bind();

        GL20.glVertexAttribPointer(
                0,
                3,
                GL11.GL_FLOAT,
                false,
                3 * Float.BYTES,
                0
        );

        GL20.glEnableVertexAttribArray(0);
        // TODO: Potentially move the above 2 things to be outside of vao or a separate method

        glDrawArrays(GL_LINES, 0, 2);

        // shader.delete();
    }
}
