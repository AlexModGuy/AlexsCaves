#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a == 0.0) {
        discard;
    }
    float animation = GameTime * 5000.0;
    float animation1 = (sin(animation) + 1) * 0.5;
    vec3 purpleWitchColor = vec3(animation1 * 0.15 + 0.85, 0, animation1 * 0.15 + 0.85);
    fragColor = vec4(purpleWitchColor * ColorModulator.rgb * vertexColor.rgb, ColorModulator.a);
}
