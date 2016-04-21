package com.reanstudio.imagefilter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.reanstudio.imagefilter.mesh.SimplePlane;
import com.reanstudio.imagefilter.renderer.MeshRenderer;

public class JaywayMainActivity extends AppCompatActivity {

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
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }

    private void initialize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (hasGLES20()) {
            glView = new GLSurfaceView(this);
//            glView.setRenderer(new OpenGLRenderer());
//            glView.setRenderer(new MeshRenderer());
            MeshRenderer meshRenderer = new MeshRenderer();

            SimplePlane simplePlane = new SimplePlane();
            simplePlane.z = 1.7f;
            simplePlane.rx = -65;
            simplePlane.loadBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.jay));
            meshRenderer.addMesh(simplePlane);
            glView.setRenderer(meshRenderer);

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