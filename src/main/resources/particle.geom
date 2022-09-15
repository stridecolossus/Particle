#version 450

layout(binding=0) uniform Projection {
    mat4 projection;
};

layout(points) in;

layout(triangle_strip, max_vertices=4) out;

layout(location=0) in vec4[] inColour;

layout(location=0) out vec2 outCoords;
layout(location=1) out vec4 outColour;

layout(constant_id=1) const float SIZE = 0.025;

void vertex(vec4 pos, float x, float y) {
    gl_Position = projection * (pos + vec4(x * SIZE, y * SIZE, 0, 0));
    outCoords = vec2((x + 1) / 2, (1 - y) / 2);
    outColour = inColour[0];
    EmitVertex();
}

void main() {
    vec4 pos = gl_in[0].gl_Position;
    vertex(pos, -1, +1);
    vertex(pos, -1, -1);
    vertex(pos, +1, +1);
    vertex(pos, +1, -1);
    EndPrimitive();
}
