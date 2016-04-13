package com.reanstudio.imagefilter.renderer;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.reanstudio.imagefilter.mesh.Cube;
import com.reanstudio.imagefilter.mesh.Group;
import com.reanstudio.imagefilter.mesh.Mesh;
import com.reanstudio.imagefilter.mesh.Plane;
import com.reanstudio.imagefilter.shape.FlatColoredSquare;
import com.reanstudio.imagefilter.shape.SmoothColoringSquare;
import com.reanstudio.imagefilter.shape.Square;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yahyamukhlis on 4/12/16.
 */
public class MeshRenderer implements GLSurfaceView.Renderer {

    private Mesh root;

    public MeshRenderer() {
        Group group = new Group();
        Cube cube = new Cube(1, 1, 1);
        cube.rx = 45;
        cube.ry = 45;
        group.add(cube);
        root = group;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background color to black ( rgba ).
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);
        // Depth buffer setup.
        gl.glClearDepthf(1.0f);
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // The type of depth testing to do.
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // Really nice perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Sets the current view port to the new size.
        gl.glViewport(0, 0, width, height);
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        gl.glLoadIdentity();
        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
                100.0f);
        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // Replace the current matrix with the identity matrix
        gl.glLoadIdentity();
        // Translates 4 units into the screen.
        gl.glTranslatef(0, 0, -10);

        root.draw(gl);
    }
}
