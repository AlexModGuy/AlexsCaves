#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    vec4 almostFinalColor = color * linear_fog_fade(vertexDistance, FogStart, FogEnd);

    float targetR = .9;
    float targetG = .3;
    float targetB = .05;

    float colorVal = almostFinalColor.r;
    if(almostFinalColor.g > colorVal){
        colorVal = almostFinalColor.g;
    }
    if(almostFinalColor.b > colorVal){
        colorVal = almostFinalColor.b;
    }
    fragColor = vec4(colorVal * targetR, colorVal * targetG, colorVal * targetB, almostFinalColor.a);

}