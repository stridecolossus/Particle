#version 450

//layout(location=0) in vec4 inColour;
layout(location=0) in vec2 inCoord;

layout(location=0) out vec4 outColour;

void main() {
//    outColour = inColour;

    float alpha = 1 - dot(inCoord, inCoord);
    if(alpha < 0.2) {
        discard;
    }
    outColour = vec4(alpha);
}
