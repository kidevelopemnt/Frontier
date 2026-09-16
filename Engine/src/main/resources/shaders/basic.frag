#version 330 core

in vec2 fragmentTexCoord;
in vec3 fragmentNormal;

uniform sampler2D textureSampler;

uniform vec3 ambientLight;
uniform vec3 directionalLight;
uniform vec3 lightDirection;

out vec4 color;

void main()
{
    float directional = max(
        dot(fragmentNormal, lightDirection),
        0.0
    );

    vec3 lighting =
        ambientLight +
        directionalLight * directional;

    vec4 textureColor = texture(textureSampler, fragmentTexCoord);

    color = textureColor * vec4(lighting, 1.0);
}