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
	
	vec4 color = texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + cos(T.x * 6.0 + u_time * 2.0) * 0.07 / u_multi * T.x)).rgba * v_color;
	
	float multi = cos(T.x * 6.0 + u_time * 2.0) * 0.07 * T.x;
	color.r *= 1.0 + multi * 10.0;
	color.g *= 1.0 + multi * 10.0;
	color.b *= 1.0 + multi * 10.0;
	
	gl_FragColor = color;
}