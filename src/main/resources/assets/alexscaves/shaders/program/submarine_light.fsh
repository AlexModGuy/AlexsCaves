#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform mat4 ProjMat;
uniform vec2 OutSize;
uniform vec2 ScreenSize;
uniform float _FOV;

in vec2 texCoord;
out vec4 fragColor;

float near = 0.1;
float far = 1000.0;
float exposure = 100.1;
float AOE = 15.;

float LinearizeDepth(float depth) 
{
    float z = depth * 2.0f - 1.0f;
    return (near * far) / (far + near - z * (far - near));    
}

void main(){
    float depth = LinearizeDepth(texture(DiffuseDepthSampler, texCoord).r);
    float distance = length(vec3(1., (2.*texCoord - 1.) * vec2(ScreenSize.x/ScreenSize.y, 1.) * tan(radians(_FOV / 2.))) * depth);
    vec2 uv = texCoord;
    float d = sqrt(pow((uv.x - 0.5),2.0) + pow((uv.y - 0.5),2.0));
	d = exp(-(d * AOE)) * exposure / (distance*0.01);
	if(depth > 500){
	    fragColor = vec4(texture(DiffuseSampler,texCoord).rgb*clamp(1.0 + d, 0.0, 10.0),1.0);
	}else{
        fragColor = texture(DiffuseSampler,texCoord);
	}
}
