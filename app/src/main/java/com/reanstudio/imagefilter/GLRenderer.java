package com.reanstudio.imagefilter;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yahyamukhlis on 4/12/16.
 */
public abstract class GLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = GLRenderer.class.getSimpleName();
    private boolean isFirstDraw;
    private boolean isSurfaceCreated;
    private int width;
    private int height;
    private long lastTime;
    private int fps;

    public GLRenderer() {
        isFirstDraw = true;
        isSurfaceCreated = false;
        width = -1;
        height = -1;
        lastTime = System.currentTimeMillis();
        fps = 0;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "Surface created.");
        isSurfaceCreated = true;
        width = -1;
        height = -1;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (!isSurfaceCreated && width == this.width && height == this.height) {
            Log.i(TAG, "Surface changed but already handled.");
            return;
        }

        String msg = "Surface changed width: " + width + " height: " + height;
        if (isSurfaceCreated) {
            msg += "context lost.";
        } else {
            msg += ".";
        }
        Log.i(TAG, msg);

        this.width = width;
        this.height = height;
        onCreate(this.width, this.height, isSurfaceCreated);
        isSurfaceCreated = false;


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame(isFirstDraw);

        fps++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 1000) {
            fps = 0;
            lastTime = currentTime;
        }

        if (isFirstDraw) {
            isFirstDraw = false;
        }
    }

    public int getFps() {
        return fps;
    }

    public abstract void onCreate(int width, int height, boolean contextLost);

    public abstract void onDrawFrame(boolean isFirstDraw);
}
