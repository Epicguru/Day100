#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
uniform float u_time;

void main() {
	vec2 T = v_texCoords.xy;
	vec4 color = texture2D(u_texture, v_texCoords).rgba * v_color;
	vec4 finalColor = vec4(color.r, color.g, color.b - cos(u_time * 10.0) + v_texCoords.y, color.a);
	
	
	gl_FragColor = finalColor;
}