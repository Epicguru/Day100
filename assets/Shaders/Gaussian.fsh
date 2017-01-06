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


// THIS IS NOT MINE - https://www.shadertoy.com/view/Mtl3Rj - Created by 'Loadus' in 2015-Feb-9
// Original shader on above link by 'CeeJayDK'
// Thanks!

float SCurve (float x) {
	
    
    // ---- by CeeJayDK

	x = x * 2.0 - 1.0;
	return -x * abs(x) * 0.5 + x + 0.5;
		
        //return dot(vec3(-x, 2.0, 1.0 ),vec3(abs(x), x, 1.0)) * 0.5; // possibly faster version
	

    
    
    // ---- original for posterity
    
    // How to do this without if-then-else?
    // +edited the too steep curve value
    
    // if (value < 0.5)
    // {
    //    return value * value * 2.0;
    // }
    
    // else
    // {
    // 	value -= 1.0;
    
    // 	return 1.0 - value * value * 2.0;
    // }
}

vec4 BlurH (sampler2D source, vec2 uv, float radius) {

	if (radius >= 1.0)
	{
		vec4 A = vec4(0.0); 
		vec4 C = vec4(0.0); 

		float width = 1.0 / 518.0;

		float divisor = 0.0; 
        float weight = 0.0;
        
        float radiusMultiplier = 1.0 / radius;
        
		for (float x = -radius; x <= radius; x++)
		{
			A = texture2D(source, uv + vec2(x * width, 0.0));     
            weight = SCurve(1.0 - (abs(x) * radiusMultiplier));             
            C += A * weight;             
			divisor += weight; 
		}

		return vec4(C.r / divisor, C.g / divisor, C.b / divisor, texture2D(source, uv).a);
	}

	return texture2D(source, uv);
}


void main(){
    
    vec2 uv = v_texCoords.xy;
    
    // Apply horizontal blur to final output
	gl_FragColor = BlurH(u_texture, uv, 10.0);
}