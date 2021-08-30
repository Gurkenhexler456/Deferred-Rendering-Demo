#version 330 core

in vec3 vf_Position;
in vec2 vf_UV;
in vec3 vf_Normal;

out vec3 out_Color;

uniform sampler2D u_Texture;

void main() {

	out_Color 		= texture(u_Texture, vf_UV).xyz;
}
