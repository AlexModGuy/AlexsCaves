#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    float animation = GameTime * 2000.0;
    float animation1 = sin(animation) + 1;
    vec4 defaultColor = texture(Sampler0, texCoord0) * vertexColor;
    vec4 color = vec4(0, animation1 * 0.15 + 0.85, animation1 * 0.15 + 0.85, defaultColor.a);
    fragColor = color * ColorModulator;
}