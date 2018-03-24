package com.example.allenhsu.camerapartice;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.support.annotation.RawRes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDetachShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by allenhsu on 2018/3/24.
 */

public class CameraRender implements GLSurfaceView.Renderer {
    public interface RenderDelegate {
        void onSurfaceCreated();
    }
    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";
    public static final String RESOLUTION = "iResolution";


    protected SurfaceTexture surfaceTexture;
    protected int textureId = -1;
    protected int shaderProgram = -1;
    protected int vertextProgram = -1;
    protected int fragmentProgram = -1;
    protected boolean isChangeShader = false;

    protected String vertexShaderRes = "";
    protected String fragmentShaderRes = "";

    private float[] transformMatrix = new float[16];
    private FloatBuffer dataBuffer;
    private int positionLocation = -1;
    private int textureCoordLocation = -1;
    private int textureMatrixLocation = -1;
    private int textureSamplerLocation = -1;
    private int iResolutionLocation = -1;
    private int[] fBOIds = new int[1];

    private boolean updateTexture = false;
    private int width;
    private int height;

    private RenderDelegate renderDelegate;

    private static final float[] vertexData = {
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    public CameraRender() {

    }

    public void setRenderDelegate(RenderDelegate renderDelegate) {
        this.renderDelegate = renderDelegate;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        initSurfaceTexture();
        dataBuffer = createBuffer(vertexData);
        if(renderDelegate != null) {
            renderDelegate.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        if (isChangeShader) {
            linkProgram(loadShader(GL_VERTEX_SHADER, vertexShaderRes),
                loadShader(GL_FRAGMENT_SHADER, fragmentShaderRes));
            isChangeShader = false;
        }


        if(updateTexture) {
            if (surfaceTexture != null) {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(transformMatrix);
            }
            updateTexture = false;
            positionLocation = glGetAttribLocation(shaderProgram, POSITION_ATTRIBUTE);
            textureCoordLocation = glGetAttribLocation(shaderProgram, TEXTURE_COORD_ATTRIBUTE);
            textureMatrixLocation = glGetUniformLocation(shaderProgram, TEXTURE_MATRIX_UNIFORM);
            textureSamplerLocation = glGetUniformLocation(shaderProgram, TEXTURE_SAMPLER_UNIFORM);

            glUniform1i(textureSamplerLocation, 0);
            glUniformMatrix4fv(textureMatrixLocation, 1, false, transformMatrix, 0);
            if (dataBuffer != null) {
                dataBuffer.position(0);
                glEnableVertexAttribArray(positionLocation);
                glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 16, dataBuffer);

                dataBuffer.position(2);
                glEnableVertexAttribArray(textureCoordLocation);
                glVertexAttribPointer(textureCoordLocation, 2, GL_FLOAT, false, 16, dataBuffer);
            }
        }

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public FloatBuffer createBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }


    protected void initSurfaceTexture() {
        textureId = Utils.createOESTextureObject();
        surfaceTexture = new SurfaceTexture(textureId);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void changeShader(String vertexShader, String fragmentShader) {
        isChangeShader = true;
        this.vertexShaderRes = vertexShader;
        this.fragmentShaderRes = fragmentShader;
    }

    protected int loadShader(int type, String shaderSource) {
        int shader = glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("Create Shader Failed!" + glGetError());
        }
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        return shader;
    }

    protected void linkProgram(int verShader, int fragShader) {

        shaderProgram = glCreateProgram();
        if (shaderProgram == 0) {
            throw new RuntimeException("Create Program Failed!" + glGetError());
        }
        glAttachShader(shaderProgram, verShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);
    }

    public void updateTexture() {
        updateTexture = true;
    }
}
