#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
uniform vec2 u_multi; 
uniform vec2 u_sub; 
uniform float u_time;

void main() {
	vec2 T = (v_texCoords - u_sub) * u_multi;
	vec2 pos = vec2(v_texCoords);
	
	vec2 distorsion = vec2(cos(u_time * 2.0 + v_texCoords.x + 7.0) * 0.15, sin(u_time * 2.0 + v_texCoords.y * 7.0) * 0.15);
	
	float distanceX = 0.5 - abs(0.5 - T.x);
	float distanceY = 0.5 - abs(0.5 - T.y);
	distorsion.x *= distanceX;
	distorsion.y *= distanceY;
	
	pos += distorsion;
	vec4 color = texture2D(u_texture, pos).rgba * v_color;
	gl_FragColor = color;
}