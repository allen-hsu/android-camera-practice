#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
const vec2 texSize = vec2(640., 640.);
const vec2 mosaicSize = vec2(8., 8.);
void main() {
    vec4 color;
   //float ratio = texSize.y/texSize.x;

   vec2 xy = vec2(vTextureCoord.x * texSize.x, vTextureCoord.y * texSize.y);

   vec2 xyMosaic = vec2(floor(xy.x / mosaicSize.x) * mosaicSize.x,
         floor(xy.y / mosaicSize.y) * mosaicSize.y );

   //which one mosaic
   vec2 xyFloor = vec2(floor(mod(xy.x, mosaicSize.x)),
                  floor(mod(xy.y, mosaicSize.y)));
   #if 0
   if((xyFloor.x == 0 || xyFloor.y == 0))
   {
      color = vec4(1., 1., 1., 1.);
   }
   else
   #endif
   {
      vec2 uvMosaic = vec2(xyMosaic.x / texSize.x, xyMosaic.y / texSize.y);
      color = texture2D( uTextureSampler, uvMosaic );
   }

   gl_FragColor = color;
}