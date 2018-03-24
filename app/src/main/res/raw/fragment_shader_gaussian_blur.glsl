#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;

vec3 mosaic(vec2 position){
	vec2 p = floor(position)/10.;
	return texture2D(uTextureSampler, p).rgb;
}

vec2 sw(vec2 p) {return vec2( floor(p.x) , floor(p.y) );}
vec2 se(vec2 p) {return vec2( ceil(p.x) , floor(p.y) );}
vec2 nw(vec2 p) {return vec2( floor(p.x) , ceil(p.y) );}
vec2 ne(vec2 p) {return vec2( ceil(p.x) , ceil(p.y) );}

vec3 blur(vec2 p) {
	vec2 inter = smoothstep(0., 1., fract(p));
	vec3 s = mix(mosaic(sw(p)), mosaic(se(p)), inter.x);
	vec3 n = mix(mosaic(nw(p)), mosaic(ne(p)), inter.x);
	return mix(s, n, inter.y);
}

void main() {
  gl_FragColor = vec4(blur(vTextureCoord)*1., 1.0);
}
