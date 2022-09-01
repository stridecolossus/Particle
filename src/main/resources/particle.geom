#version 450

layout(points) in;

layout(binding=0) uniform UniformBuffer {
    mat4 modelview;
    mat4 projection;
};

layout(triangle_strip, max_vertices=4) out;

layout(location=0) out vec2 outCoord;
//layout(location=1) out vec4 outColour;

const float SIZE = 0.1;

void main() {

    vec4 pos = gl_in[0].gl_Position;
    
    gl_Position = projection * (pos + vec4(-SIZE, SIZE, 0, 0));
    outCoord = vec2(-1, 1);
//    outColour = inColour;
    EmitVertex();

    gl_Position = projection * (pos + vec4(-SIZE, -SIZE, 0, 0));
    outCoord = vec2(-1, -1);
//    outColour = inColour;
    EmitVertex();

    gl_Position = projection * (pos + vec4(SIZE, SIZE, 0, 0));
    outCoord = vec2(1, 1);
//   outColour = inColour;
    EmitVertex();

    gl_Position = projection * (pos + vec4(SIZE, -SIZE, 0, 0));
    outCoord = vec2(1, -1);
//    outColour = inColour;
    EmitVertex();

    EndPrimitive();
}
