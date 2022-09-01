#version 450

layout(location=0) in vec3 pos;
//layout(location=1) in vec4 col;

//layout(push_constant) uniform Matrix {
//    mat4 matrix;
//};

layout(binding=0) uniform UniformBuffer {
    mat4 modelview;
    mat4 projection;
};


//layout(location=0) out vec4 outColour;

void main() {
    gl_PointSize = 1;
    gl_Position = modelview * vec4(pos, 1.0);
//    outColour = col;
}
