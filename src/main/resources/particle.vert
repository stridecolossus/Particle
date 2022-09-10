#version 450

//layout(push_constant) uniform Constants {
//    float time;
//    mat4 matrix;
//};

layout(binding=0) uniform Projection {
    mat4 modelview;
    mat4 projection;
};

layout(constant_id=2) const int lifetime = 5000;

layout(location=0) in vec3 pos;
layout(location=1) in float created;

layout(location=0) out float age;

void main() {
    gl_PointSize = 1;
    gl_Position = modelview * vec4(pos, 1.0);
    age = 1 - created; // TODO
    // age = (time - created) / lifetime;
}
