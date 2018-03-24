package com.example.allenhsu.camerapartice;

import android.Manifest;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private CameraRender cameraRender = new CameraRender();
    private BottomNavigationView bottomNavigationView;
    private WeakReference<Context> weakReference;

    @RawRes int vertexShader = R.raw.vertex_shader_base;
    @RawRes int[] randomFragmentShader = {
            R.raw.fragment_shader_blur,
            R.raw.fragment_shader_mosaic,
            R.raw.fragment_shader_sketch,
            R.raw.fragment_shader_gray,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakReference = new WeakReference<Context>(this);
        setContentView(R.layout.activity_main);
        glSurfaceView = findViewById(R.id.camera_preview);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(cameraRender);
        cameraRender.changeShader(Utils.readShaderFromResource(this, vertexShader), Utils.readShaderFromResource(this, R.raw.fragment_shader_base));
        MainActivityPermissionsDispatcher.setupCameraWithPermissionCheck(this);
        cameraRender.setRenderDelegate(renderDelegate);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_normal:
                    cameraRender.changeShader(Utils.readShaderFromResource(weakReference.get(), vertexShader), Utils.readShaderFromResource(weakReference.get(), R.raw.fragment_shader_base));
                    break;
//                case R.id.navigation_random:
//                    cameraRender.changeShader(Utils.readShaderFromResource(weakReference.get(), vertexShader), Utils.readShaderFromResource(weakReference.get(), getRandomShader()));
//                    break;
                case R.id.navigation_blur:
                    cameraRender.changeShader(Utils.readShaderFromResource(weakReference.get(), vertexShader), Utils.readShaderFromResource(weakReference.get(), R.raw.fragment_shader_blur));
                    break;
                case R.id.navigation_gray:
                    cameraRender.changeShader(Utils.readShaderFromResource(weakReference.get(), vertexShader), Utils.readShaderFromResource(weakReference.get(), R.raw.fragment_shader_gray));
                    break;
                case R.id.navigation_sketch:
                    cameraRender.changeShader(Utils.readShaderFromResource(weakReference.get(), vertexShader), Utils.readShaderFromResource(weakReference.get(), R.raw.fragment_shader_sketch));
                    break;
                case R.id.navigation_mosaic:
                    cameraRender.changeShader(Utils.readShaderFromResource(weakReference.get(), vertexShader), Utils.readShaderFromResource(weakReference.get(), R.raw.fragment_shader_mosaic));
                    break;
            }
            return true;
        }
    };

    private CameraRender.RenderDelegate renderDelegate = new CameraRender.RenderDelegate() {
        @Override
        public void onSurfaceCreated() {
            Camera2Manager.getInstance().startCameraThread();
            Camera2Manager.getInstance().openCamera(weakReference.get(), cameraRender.getSurfaceTexture());
            cameraRender.getSurfaceTexture().setDefaultBufferSize(Camera2Manager.getInstance().getPreviewSize().getWidth(),
                Camera2Manager.getInstance().getPreviewSize().getHeight());
            cameraRender.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
                cameraRender.updateTexture();
                glSurfaceView.requestRender();
            });
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraRender.setRenderDelegate(null);
        Camera2Manager.getInstance().closeCamera();
        Camera2Manager.getInstance().stopCameraThread();
    }


    @NeedsPermission(Manifest.permission.CAMERA)
    public void setupCamera() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Camera2Manager.getInstance().setupCamera(this, dm.widthPixels, dm.heightPixels);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        Toast.makeText(this, R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }

    private @RawRes int getRandomShader() {
        int random = (int)(Math.random()*randomFragmentShader.length);
        return randomFragmentShader[random];
    }
}
