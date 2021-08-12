#version 330 core

in vec3 in_Position;
in vec2 in_UV;
in vec3 in_Normal;

out vec3 vf_Position;
out vec2 vf_UV;
out vec3 vf_Normal;

uniform mat4 u_Projection;
uniform mat4 u_View;
uniform mat4 u_Model;

void main() {

	vec4 position 	= u_Model * vec4(in_Position, 1.0);

	vf_Position 	= position.xyz;
	vf_UV 			= in_UV;
	vf_Normal 		= (in_Normal + 1.0) * 0.5;

	gl_Position = u_Projection * u_View * position;
}
