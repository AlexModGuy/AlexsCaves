#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D BlurSampler;

uniform mat4 ProjMat;
uniform vec2 OutSize;
uniform vec2 ScreenSize;
uniform float _FOV;

in vec2 texCoord;
in vec2 scaledCoord;

uniform vec2 InSize;
uniform vec4 Scissor;
uniform vec4 Vignette;

out vec4 fragColor;

void main() {
    vec4 ScaledTexel = texture(DiffuseSampler, texCoord);
    vec4 BlurTexel = texture(BlurSampler, texCoord);
    float distance = length(vec3(1., (2.*texCoord - 1.) * vec2(ScreenSize.x/ScreenSize.y, 1.) * tan(radians(_FOV / 2.))));
    float circleDistance = (distance * distance);
    vec4 OutTexel = ScaledTexel - BlurTexel + BlurTexel * circleDistance;
    fragColor = vec4(OutTexel.rgb, 1.0);
}
