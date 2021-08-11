#version 330 core

in vec3 in_Position;
in vec2 in_UV;
in vec3 in_Normal;

out vec3 vf_Position;
out vec2 vf_UV;
out vec3 vf_Normal;

void main() {

	vf_Position 	= in_Position;
	vf_UV 			= in_UV;
	vf_Normal 		= (in_Normal + 1.0) * 0.5;
	
	vec3 position 	= in_Position;
	position		-= vec3(0.2, 0.3, 0.1) * position.z;

	gl_Position = vec4(position, 1.0);
}
