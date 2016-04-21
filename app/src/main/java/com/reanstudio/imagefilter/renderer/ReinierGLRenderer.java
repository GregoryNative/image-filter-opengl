package com.reanstudio.imagefilter.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.reanstudio.imagefilter.shader.ReinierGraphicTools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yahyamukhlis on 4/19/16.
 */
public class ReinierGLRenderer implements GLSurfaceView.Renderer {

    // Our matrices
    private final float[] trxProjection = new float[16];
    private final float[] trxView = new float[16];
    private final float[] trxProjectionAndView = new float[16];

    // Geometric variables
    public static float vertices[];
    public static short indices[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;

    // Our screen resolution
    float screenWidth = 1280;
    float screenHeight = 768;

    private Context context;
    private long lastTime;
    private int program;

    public ReinierGLRenderer(Context context) {
        this.context = context;
        lastTime = System.currentTimeMillis() + 100;
    }

    public void onPause() {

    }

    public void onResume() {
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Create the triangle
        SetupTriangle();

        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

        // Create the shaders
        int vertexShader = ReinierGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, ReinierGraphicTools.VS_SOLID_COLOR);
        int fragmentShader = ReinierGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, ReinierGraphicTools.FS_SOLID_COLOR);

        ReinierGraphicTools.SP_SOLID_COLOR = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(ReinierGraphicTools.SP_SOLID_COLOR, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(ReinierGraphicTools.SP_SOLID_COLOR, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(ReinierGraphicTools.SP_SOLID_COLOR);                  // creates OpenGL ES program executables

        // Set our shader programm
        GLES20.glUseProgram(ReinierGraphicTools.SP_SOLID_COLOR);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // We need to know the current width and height.
        screenWidth = width;
        screenHeight = height;

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int)screenWidth, (int)screenHeight);

        // Clear our matrices
        for(int i=0;i<16;i++)
        {
            trxProjection[i] = 0.0f;
            trxView[i] = 0.0f;
            trxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(trxProjection, 0, 0f, screenWidth, 0.0f, screenHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(trxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(trxProjectionAndView, 0, trxProjection, 0, trxView, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Get the current time
        long now = System.currentTimeMillis();

        // We should make sure we are valid and sane
        if (lastTime > now) return;

        // Get the amount of time the last frame took.
        long elapsed = now - lastTime;

        // Update our example

        // Render our example
        draw(trxProjectionAndView);

        // Save the current time to see how long it took :).
        lastTime = now;
    }

    private void draw(float[] m) {
        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(ReinierGraphicTools.SP_SOLID_COLOR, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(ReinierGraphicTools.SP_SOLID_COLOR, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void SetupTriangle() {
        // We have to create the vertices of our triangle.
        vertices = new float[]
        {
            10.0f, 200f, 0.0f,
            10.0f, 100f, 0.0f,
            100f, 100f, 0.0f,
        };

        indices = new short[] {0, 1, 2}; // The order of vertexrendering.

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }
}
