package com.reanstudio.imagefilter.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.reanstudio.imagefilter.renderer.LessonOneRenderer;

/**
 * Created by yahyamukhlis on 4/26/16.
 */
public class LessonOneActivity extends Activity {

    /**
     * Hold a refernce to our GLSurfaceView
     */
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context
            glSurfaceView.setEGLContextClientVersion(2);
            // Set the renderer to our demo renderer, defined below
            glSurfaceView.setRenderer(new LessonOneRenderer());
        } else {
            // This is where you could create an OpenGL ES 1.x compatible renderer if you wanted
            // to support both ES 1 and ES 2
            return;
        }

        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity must call the GL surface view's onResume()
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The activity must call the GL surface view's onPause()
        glSurfaceView.onPause();
    }
}
