package com.reanstudio.imagefilter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private GLSurfaceView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        setContentView(glView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    private void initialize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (hasGLES20()) {
            glView = new GLSurfaceView(this);
            glView.setEGLContextClientVersion(2);
            glView.setPreserveEGLContextOnPause(true);
            glView.setRenderer(new GLES20Renderer());
        } else {
            // device not supported
        }
    }

    private boolean hasGLES20() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }
}
