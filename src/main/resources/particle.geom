#version 450

layout(binding=0) uniform UniformBuffer {
    mat4 modelview;
    mat4 projection;
};

layout(points) in;

layout(triangle_strip, max_vertices=4) out;

layout(location=0) out vec2 coords;

layout(constant_id=1) const float SIZE = 0.025;

void main() {
    vec4 pos = gl_in[0].gl_Position;
    
    gl_Position = projection * (pos + vec4(-SIZE, SIZE, 0, 0));
    coords = vec2(-1, 1);
    EmitVertex();

    gl_Position = projection * (pos + vec4(-SIZE, -SIZE, 0, 0));
    coords = vec2(-1, -1);
    EmitVertex();

    gl_Position = projection * (pos + vec4(SIZE, SIZE, 0, 0));
    coords = vec2(1, 1);
    EmitVertex();

    gl_Position = projection * (pos + vec4(SIZE, -SIZE, 0, 0));
    coords = vec2(1, -1);
    EmitVertex();

    EndPrimitive();
}
