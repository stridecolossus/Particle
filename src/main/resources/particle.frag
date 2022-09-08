#version 450

layout(location=0) in vec2 coords;
layout(location=1) in float age;

layout(location=0) out vec4 col;

void main() {
    col = vec4(1, age, age / 2, age);
}
