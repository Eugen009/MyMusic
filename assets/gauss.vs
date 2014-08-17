//uniform mat4 uMVPMatrix;
attribute vec4 a_Position;
attribute vec2 a_Texcoord;
varying vec4 v_Color;
varying vec2 v_Texcoord;

void main() {	
	gl_Position.xyz = a_Position.xyz;
	gl_Position.w = 1.0;		
	v_Texcoord = a_Texcoord;
}