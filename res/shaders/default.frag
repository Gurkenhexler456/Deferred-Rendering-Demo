#version 330 core

in vec3 vf_Position;
in vec2 vf_UV;
in vec3 vf_Normal;

out vec3 out_Position;
out vec4 out_Albedo;
out vec3 out_Normal;

uniform sampler2D u_Texture;

void main() {

	out_Position 	= vf_Position;
	out_Albedo 		= texture(u_Texture, vf_UV);//vec4(vf_UV, 0.0f, 0.0f);
	out_Normal 		= vf_Normal;
}
