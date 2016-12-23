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
	vec4 color = texture2D(u_texture, v_texCoords).rgba * v_color;	
	vec2 T = (v_texCoords - u_sub) * u_multi;
	color.a = (1.0 - T.y / 1.1) * color.a;
	float dark = 0.70;
	color.r *= dark * (1.0 - T.y); 
	color.g *= dark * (1.0 - T.y); 
	color.b *= dark * (1.0 - T.y); 
	gl_FragColor = color;
}