#version 330 core

in vec2 vf_UV;

out vec4 out_Color;

uniform sampler2D u_Albedo;
uniform sampler2D u_Position;
uniform sampler2D u_Normal;

void main() {

	vec4 color_specular = texture(u_Albedo, vf_UV);
	vec3 albedo = color_specular.xyz;
	float specular = color_specular.a;
	
	vec3 position = texture(u_Position, vf_UV).xyz;
	vec3 normal = texture(u_Normal, vf_UV).xyz;


	out_Color = vec4(albedo, 1.0);
}