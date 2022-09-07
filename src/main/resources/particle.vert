#version 450

layout(location=0) in vec3 pos;

//layout(push_constant) uniform Matrix {
//    mat4 matrix;
//};

layout(binding=0) uniform UniformBuffer {
    mat4 modelview;
    mat4 projection;
};

void main() {
    gl_PointSize = 1;
    gl_Position = modelview * vec4(pos, 1.0);
}
