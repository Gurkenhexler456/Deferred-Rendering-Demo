#version 330 core

in vec2 vf_UV;

out vec4 out_Color;

uniform sampler2D u_Color;

void main() {

	out_Color = texture(u_Color, vf_UV);
}