#version 330 core

in vec2 fragmentTexCoord;
in vec3 fragmentPosition;
in vec3 fragmentNormal;

uniform sampler2D textureSampler;

uniform vec3 ambientLight;
uniform vec3 directionalLight;
uniform vec3 lightDirection;

uniform vec3 pointLightPosition;
uniform vec3 pointLightColor;
uniform float pointLightIntensity;

out vec4 color;

void main()
{
    // Directional light
    float directional = max(
        dot(fragmentNormal, lightDirection),
        0.0
    );

    // Point light
    vec3 toLight = pointLightPosition - fragmentPosition;
    float distance = length(toLight);
    vec3 lightDirection = normalize(toLight);
    float attenuation = 1.0 / (1.0 + 0.09 * distance);  // TODO: Make constants (constant, linear, quadratic) configurable in PointLight class
    float brightness =
        max(
            dot(fragmentNormal, lightDirection),
            0.0
        );

    vec3 pointLighting =
        pointLightColor *
        pointLightIntensity *
        brightness *
        attenuation;

    // Lighting
    vec3 lighting =
        ambientLight +
        directionalLight * directional +
        pointLighting;

    vec4 textureColor = texture(textureSampler, fragmentTexCoord);

    color = textureColor * vec4(lighting, 1.0);
}