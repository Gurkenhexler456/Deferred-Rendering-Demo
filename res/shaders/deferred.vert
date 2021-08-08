#version 330 core

in vec3 in_Position;
in vec2 in_UV;

out vec2 vf_UV;

void main() {

	vf_UV = in_UV;

	gl_Position = vec4(in_Position, 1.0);
}