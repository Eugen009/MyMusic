uniform mat4 uMVPMatrix;
uniform vec4 uUVInfo;// index, width, heigth
uniform vec4 uUVOffset;// uoffset, voffset
uniform vec4 uColor;
attribute vec4 a_Position;
attribute vec2 a_Texcoord;
attribute vec4 a_Color;
varying vec4 v_Color;
varying vec2 v_Texcoord;

void main() {	
	gl_Position.xyz = a_Position.xyz;
	gl_Position.w = 1.0;
	gl_Position = uMVPMatrix * gl_Position;		
  	//gl_Position = uMVPMatrix *vPosition;
	v_Texcoord = vec2(
		(a_Texcoord.x + uUVInfo.x) * uUVInfo.z + uUVOffset.x,
		(a_Texcoord.y + uUVInfo.y) * uUVInfo.w + uUVOffset.y
	);
	v_Color = a_Color * uColor;
}