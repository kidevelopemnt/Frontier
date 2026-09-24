package frontier.engine.graphics;

import frontier.engine.ecs.components.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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
    private static final VertexBuffer vbo = new VertexBuffer(new float[0]);

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
        System.out.println("LINE");

        shader.setMatrix4f("view", view);
        shader.setMatrix4f("projection", projection);
        shader.setVector3f("color", color);

        glLineWidth(width);

        float[] vertices = {
                start.x, start.y, start.z,
                end.x,   end.y,   end.z
        };

        vao.bind();
        vbo.bind();

        glBufferData(
                GL_ARRAY_BUFFER,
                vertices,
                GL_DYNAMIC_DRAW
        );

        glDrawArrays(GL_LINES, 0, 2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        shader.delete();
    }
}
