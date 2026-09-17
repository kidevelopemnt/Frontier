#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat3 normalMatrix;

out vec2 fragmentTexCoord;
out vec3 fragmentNormal;
out vec3 fragmentPosition;

void main()
{
    gl_Position =
        projection *
        view *
        model *
        vec4(position, 1.0);

    fragmentTexCoord = texCoord;
    fragmentNormal = normalize(normalMatrix * normal);
    fragmentPosition = vec3(model * vec4(position, 1.0));
}