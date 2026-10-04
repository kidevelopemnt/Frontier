package frontier.engine.assets;

import java.util.Locale;

public enum AssetType {
    TEXTURE, MODEL, MATERIAL, OBJECT, SCENE, SHADER, UNKNOWN;

    public static AssetType fromPath(String path) {
        String normalized = path.replace('\\', '/');
        int dot = normalized.lastIndexOf('.');
        if (dot < 0 || dot == normalized.length() - 1) return UNKNOWN;
        return switch (normalized.substring(dot + 1).toLowerCase(Locale.ROOT)) {
            case "png", "jpg", "jpeg" -> TEXTURE;
            case "obj", "fbx" -> MODEL;
            case "material", "mat" -> MATERIAL;
            case "object", "prefab" -> OBJECT;
            case "scene" -> SCENE;
            case "vert", "frag", "glsl" -> SHADER;
            default -> UNKNOWN;
        };
    }
}
