#version 450

layout(push_constant) uniform Constants {
    mat4 modelview;
};

layout(location=0) in vec3 inPosition;
layout(location=1) in vec4 inColour;

layout(location=0) out vec4 outColour;

void main() {
    gl_PointSize = 1;
    gl_Position = modelview * vec4(inPosition, 1.0);
    outColour = inColour;
}
