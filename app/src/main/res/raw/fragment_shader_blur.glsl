#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
const vec3 vChangeColor = vec3(0.005,0.005,0.005);
void main() {
    vec4 nColor=texture2D(uTextureSampler,vTextureCoord);
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x-vChangeColor.r,vTextureCoord.y-vChangeColor.r));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x-vChangeColor.r,vTextureCoord.y+vChangeColor.r));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x+vChangeColor.r,vTextureCoord.y-vChangeColor.r));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x+vChangeColor.r,vTextureCoord.y+vChangeColor.r));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x-vChangeColor.g,vTextureCoord.y-vChangeColor.g));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x-vChangeColor.g,vTextureCoord.y+vChangeColor.g));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x+vChangeColor.g,vTextureCoord.y-vChangeColor.g));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x+vChangeColor.g,vTextureCoord.y+vChangeColor.g));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x-vChangeColor.b,vTextureCoord.y-vChangeColor.b));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x-vChangeColor.b,vTextureCoord.y+vChangeColor.b));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x+vChangeColor.b,vTextureCoord.y-vChangeColor.b));
    nColor+=texture2D(uTextureSampler,vec2(vTextureCoord.x+vChangeColor.b,vTextureCoord.y+vChangeColor.b));
    nColor/=13.0;
    gl_FragColor=nColor;
}
