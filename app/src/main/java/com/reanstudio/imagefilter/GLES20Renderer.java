package com.reanstudio.imagefilter;

import android.opengl.GLES20;

/**
 * Created by yahyamukhlis on 4/12/16.
 */
public class GLES20Renderer extends GLRenderer {

    @Override
    public void onCreate(int width, int height, boolean contextLost) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);
    }

    @Override
    public void onDrawFrame(boolean isFirstDraw) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
