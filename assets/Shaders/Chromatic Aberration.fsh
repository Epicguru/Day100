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
	
	float maxDst = distance(vec2(0.5, 0.5), vec2(1.0, 1.0)) / 4;
	float dst = distance(T, vec2(0.5, 0.5)) / 4;
	float amount = 0.02 * dst;
	vec3 col;
    col.r = texture2D(u_texture, vec2(v_texCoords.x + amount, v_texCoords.y + amount)).r;
    col.g = texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - amount)).g;
    col.b = texture2D(u_texture, vec2(v_texCoords.x - amount, v_texCoords.y) ).b;

	col *= (1.0 - amount * 0.5);
	gl_FragColor = vec4(col, texture2D(u_texture, v_texCoords).a);
}