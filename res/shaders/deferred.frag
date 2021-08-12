#version 330 core

#define LIGHT_COUNT 3

#define LIGHT_DIRECTIONAL 0
#define LIGHT_POINT 1

struct Light {

	vec3 position;
	vec3 color;
	int type;
};


in vec2 vf_UV;

out vec4 out_Color;


uniform vec3 u_Ambient;
uniform Light u_Lights[LIGHT_COUNT];

uniform sampler2D u_Albedo;
uniform sampler2D u_Position;
uniform sampler2D u_Normal;
uniform sampler2D u_Depth;


vec3 processDirectional(Light _light, vec3 _position, vec3 _normal) {

	vec3 direction = normalize(_light.position);
	float diff = max(dot(_normal, direction), 0);
	
	return (diff * _light.color);
}


vec3 processPoint(Light _light, vec3 _position, vec3 _normal) {

	vec3 direction = normalize(_light.position - _position);
	float diff = max(dot(_normal, direction), 0);
	
	return (diff * _light.color);
}


vec3 processLights(vec3 _position, vec3 _normal) {

	vec3 result = vec3(0.0);
	
	for(int i = 0; i < LIGHT_COUNT; i++) {
	
		Light light = u_Lights[i];
	
		switch(light.type) {
		
		case 	LIGHT_DIRECTIONAL	: 	result += processDirectional(light, _position, _normal);
										break;
		case 	LIGHT_POINT			: 	result += processPoint(light, _position, _normal);
										break;
		}
	}
	
	return result;
}


void main() {

	vec4 color_specular = texture(u_Albedo, vf_UV);
	vec3 albedo 		= color_specular.xyz;
	float specular 		= color_specular.a;

	vec3 position		= texture(u_Position, vf_UV).xyz;
	vec3 normal 		= texture(u_Normal, vf_UV).xyz;
	
	float depth 		= texture(u_Depth, vf_UV).x;

	normal = (normal * 2) - 1;

	vec3 color = processLights(position, normal);
	color += u_Ambient * vec3(albedo);

	out_Color = vec4(vec3(color), 1.0);
}
