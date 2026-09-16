package frontier.engine.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {
    private int id;
    private int width;
    private int height;

    public Texture(String path) {
        ByteBuffer imageData = loadResource(path);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            ByteBuffer image = STBImage.stbi_load_from_memory(
                    imageData,
                    widthBuffer, heightBuffer, channelsBuffer,
                    4
            );

            if (image == null) {
                throw new RuntimeException(
                        "Failed to load texture " + path + "\n" +
                        STBImage.stbi_failure_reason()
                );
            }

            width = widthBuffer.get(0);
            height = heightBuffer.get(0);

            id = GL11.glGenTextures();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

            STBImage.stbi_image_free(image);
        }
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public void delete() {
        GL11.glDeleteTextures(id);
    }

    private ByteBuffer loadResource(String path) {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(path)) {

            if (input == null) {
                throw new RuntimeException(
                        "Texture not found: " + path
                );
            }

            byte[] bytes = input.readAllBytes();

            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            return buffer;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load texture: " + path,
                    e
            );
        }
    }
}
