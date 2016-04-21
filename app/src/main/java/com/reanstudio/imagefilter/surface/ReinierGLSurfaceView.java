package com.reanstudio.imagefilter.surface;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.reanstudio.imagefilter.renderer.ReinierGLRenderer;

/**
 * Created by yahyamukhlis on 4/19/16.
 */
public class ReinierGLSurfaceView extends GLSurfaceView {

    private final ReinierGLRenderer glRenderer;

    public ReinierGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the renderer for drawing on the glsurfaceview
        glRenderer = new ReinierGLRenderer(context);
        setRenderer(glRenderer);

        // renderer the view only when there is a change in the drawing data
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onPause() {
        super.onPause();
        glRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        glRenderer.onResume();
    }
}
