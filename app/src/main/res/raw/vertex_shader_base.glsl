attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoord;
varying vec2 blurCoord1s[14];
const highp float mWidth=720.0;
const highp float mHeight=1280.0;
void main() {
  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
  gl_Position = aPosition;
  highp float mul_x = 2.0 / mWidth;
  highp float mul_y = 2.0 / mHeight;

  blurCoord1s[0] = vTextureCoord + vec2( 0.0 * mul_x, -10.0 * mul_y );
  blurCoord1s[1] = vTextureCoord + vec2( 8.0 * mul_x, -5.0 * mul_y );
  blurCoord1s[2] = vTextureCoord + vec2( 8.0 * mul_x, 5.0 * mul_y );
  blurCoord1s[3] = vTextureCoord + vec2( 0.0 * mul_x, 10.0 * mul_y );
  blurCoord1s[4] = vTextureCoord + vec2( -8.0 * mul_x, 5.0 * mul_y );
  blurCoord1s[5] = vTextureCoord + vec2( -8.0 * mul_x, -5.0 * mul_y );
  blurCoord1s[6] = vTextureCoord + vec2( 0.0 * mul_x, -6.0 * mul_y );
  blurCoord1s[7] = vTextureCoord + vec2( -4.0 * mul_x, -4.0 * mul_y );
  blurCoord1s[8] = vTextureCoord + vec2( -6.0 * mul_x, 0.0 * mul_y );
  blurCoord1s[9] = vTextureCoord + vec2( -4.0 * mul_x, 4.0 * mul_y );
  blurCoord1s[10] = vTextureCoord + vec2( 0.0 * mul_x, 6.0 * mul_y );
  blurCoord1s[11] = vTextureCoord + vec2( 4.0 * mul_x, 4.0 * mul_y );
  blurCoord1s[12] = vTextureCoord + vec2( 6.0 * mul_x, 0.0 * mul_y );
  blurCoord1s[13] = vTextureCoord + vec2( 4.0 * mul_x, -4.0 * mul_y );
}