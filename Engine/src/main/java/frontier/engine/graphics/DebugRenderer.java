package frontier.engine.graphics;

import frontier.engine.ecs.components.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;
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
        int lifespan;  // negative numbers indicate infinite

        DebugLine(Vector3f start, Vector3f end, Vector3f color, float width, int lifespan) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.width = width;
            this.lifespan = lifespan;
        }
    }

    private static final List<DebugLine> lines = new ArrayList<>();

    public static void drawBox(Vector3f center, Vector3f size, Vector3f color, float width, int lifespan) {
        Vector3f halfSize = new Vector3f(size).mul(0.5f);

        float minX = center.x - halfSize.x;
        float maxX = center.x + halfSize.x;
        float minY = center.y - halfSize.y;
        float maxY = center.y + halfSize.y;
        float minZ = center.z - halfSize.z;
        float maxZ = center.z + halfSize.z;

        // Bottom
        drawLine(
                new Vector3f(minX, minY, minZ),
                new Vector3f(maxX, minY, minZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(maxX, minY, minZ),
                new Vector3f(maxX, minY, maxZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(maxX, minY, maxZ),
                new Vector3f(minX, minY, maxZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(minX, minY, maxZ),
                new Vector3f(minX, minY, minZ),
                color, width, lifespan
        );

        // Top
        drawLine(
                new Vector3f(minX, maxY, minZ),
                new Vector3f(maxX, maxY, minZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(maxX, maxY, minZ),
                new Vector3f(maxX, maxY, maxZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(maxX, maxY, maxZ),
                new Vector3f(minX, maxY, maxZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(minX, maxY, maxZ),
                new Vector3f(minX, maxY, minZ),
                color, width, lifespan
        );

        // Vertical edges
        drawLine(
                new Vector3f(minX, minY, minZ),
                new Vector3f(minX, maxY, minZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(maxX, minY, minZ),
                new Vector3f(maxX, maxY, minZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(maxX, minY, maxZ),
                new Vector3f(maxX, maxY, maxZ),
                color, width, lifespan
        );

        drawLine(
                new Vector3f(minX, minY, maxZ),
                new Vector3f(minX, maxY, maxZ),
                color, width, lifespan
        );
    }

    public static void drawBox(
            Vector3f center,
            Vector3f size,
            Vector3f color,
            float width
    ) {
        drawBox(center, size, color, width, 60);
    }

    public static void drawLine(Vector3f start, Vector3f end, Vector3f color, float width, int lifespan) {
        lines.add(new DebugLine(
                new Vector3f(start),
                new Vector3f(end),
                new Vector3f(color),
                width,
                lifespan
        ));
    }

    public static void drawLine(Vector3f start, Vector3f end, Vector3f color, float width) {
        lines.add(new DebugLine(
                new Vector3f(start),
                new Vector3f(end),
                new Vector3f(color),
                width,
                60  // TODO: Is there a way to get FPS and do 1 second as default?
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

    public static void render(Camera camera, float aspectRatio, float alpha) {
        Iterator<DebugLine> iterator = lines.iterator();

        while (iterator.hasNext()) {
            DebugLine line = iterator.next();
            renderLine(line.start, line.end, line.color, line.width, camera.getViewMatrix(alpha), camera.getProjectionMatrix(aspectRatio));
            line.lifespan -= 1;
            if (line.lifespan == 0) {
                iterator.remove();
            }
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
