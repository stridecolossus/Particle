#version 450

layout(binding=1) uniform sampler2D Sampler;

layout(location=0) in vec2 inCoords;
layout(location=1) in vec4 inColour;

layout(location=0) out vec4 outColour;

void main() {
    outColour = inColour * texture(Sampler, inCoords);
}
