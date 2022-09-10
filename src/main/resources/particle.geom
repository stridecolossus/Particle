#version 450

layout(binding=0) uniform UniformBuffer {
    mat4 modelview;
    mat4 projection;
};

layout(points) in;

layout(triangle_strip, max_vertices=4) out;

layout(location=0) in float[] ages;

layout(location=0) out vec2 coords;
layout(location=1) out float age;

layout(constant_id=1) const float size = 0.025;

void vertex(vec4 pos, float x, float y) {
    coords = vec2(x, y);
    gl_Position = projection * (pos + vec4(x * size, y * size, 0, 0));
    age = ages[0];
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
