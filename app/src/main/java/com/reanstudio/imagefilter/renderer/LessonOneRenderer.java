package com.reanstudio.imagefilter.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yahyamukhlis on 4/26/16.
 */
public class LessonOneRenderer implements GLSurfaceView.Renderer {

    /**
     * Store our modle data in a float buffer
     */
    private final FloatBuffer triangle1Vertices;
    private final FloatBuffer triangle2Vertices;
    private final FloatBuffer triangle3Vertices;

    /**
     * How many bytes per float
     */
    private final int bytesPerFloat = 4;

    /**
     * Store the view matrix. This can be thought o as our camera
     */
    private float[] viewMatrix = new float[16];

    final String vertexShader =
        "uniform mat4 u_MVPMatrix;                  \n" + // A constant representing the combined model/view/projection matrix
        "attribute vec4 a_Position;                 \n" + // Per-vertex position information we will pass in
        "attribute vec4 a_Color;                    \n" + // Per-vertex color information we will pass in
        "varying vec4 v_Color;                      \n" + // This will be passed into the fragment shader
        "void main()                                \n" + // The entry point for our vertex shader
        "{                                          \n" +
        "   v_Color = a_Color;                      \n" + // Pass the color through to the fragment shader
        "   gl_Position = u_MVPMatrix * a_Position; \n" +
        "}                                          \n";

    final String fragmentShader =
        "precision mediump float;       \n" + // Set the default precision to medium
        "varying vec4 v_Color;          \n" + // This is the color from the vertex shader interpolated across the triangle per fragment
        "void main()                    \n" + // The entry point for our fragment shader
        "{                              \n" +
        "   gl_FragColor = v_Color;     \n" + // Pass the color directly through the pipeline
        "}                              \n";

    /**
     * This will be used to pass in the transformation matrix
     */
    private int mvpMatrixHandle;

    /**
     * This will be used to pass in model position information
     */
    private int positionHandle;

    /**
     * This will be used to pass in model color information
     */
    private int colorHandle;

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport
     */
    private float[] projectionMatrix = new float[16];

    /**
     * Store the model matrix. This matrix is used to move models from object space
     */
    private float[] modelMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program
     */
    private float[] mvpMatrix = new float[16];

    /**
     * How many elements per vertex
     */
    private final int strideBytes = 7 * bytesPerFloat;

    /**
     * Offset of the position data
     */
    private final int positionOffset = 0;

    /**
     * Size of the position data in elements
     */
    private final int positionDataSize = 3;

    /**
     * Offset of the color data
     */
    private final int colorOffset = 3;

    /**
     * Size of the color data in elements
     */
    private final int colorDataSize = 4;

    public LessonOneRenderer() {
        // Define points for equilateral triangles

        // This triangle is red, green and blue
        final float[] triangle1VerticesData = {
            // X, Y, Z
            // R, G, B, A
            -0.5f, -0.25f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 0.559016994f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
        };

        // This triangle is yellow, cyan and magenta
        final float[] triangle2VerticesData = {
            -0.5f, -0.25f, 0.0f,
            1.0f, 1.0f, 0.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f,

            0.0f, 0.559016994f, 0.0f,
            1.0f, 0.0f, 1.0f, 1.0f};

        // This triangle is white, gray, and black.
        final float[] triangle3VerticesData = {
                -0.5f, -0.25f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f};

        // Initialize the buffers
        triangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        triangle2Vertices = ByteBuffer.allocateDirect(triangle2VerticesData.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        triangle3Vertices = ByteBuffer.allocateDirect(triangle3VerticesData.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        triangle1Vertices.put(triangle1VerticesData).position(0);
        triangle2Vertices.put(triangle2VerticesData).position(0);
        triangle3Vertices.put(triangle3VerticesData).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background clear color to gray
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Position the eye behind the origin
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.05f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        // Load in the vertex shader
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0) {
            // Pass in the shader source
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }

        // Load in the fragment shader.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0) {
            // Pass in the shader source
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader
            GLES20.glCompileShader(fragmentShaderHandle);

            // Get the compilation status
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0) {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            // Bind the vertex shader to the program
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shader together into a program
            GLES20.glLinkProgram(programHandle);

            // Get the link status
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program
        mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        colorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        // Tell OpenGL to use this program when rendering
        GLES20.glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Draw the triangle facing straight on
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(triangle1Vertices);

        // Draw one translated a bit down and rotated to be flat on the ground
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, -1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(triangle2Vertices);

        //Draw one translated a bit to the right and rotated to be facing to the left
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, 90.0f, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(triangle3Vertices);
    }

    /**
     * Draws a triangle from the given vertex data
     *
     * @param triangleBuffer the buffer containing the vertex data
     */
    private void drawTriangle(final FloatBuffer triangleBuffer) {
        // Pass in the position information
        triangleBuffer.position(positionOffset);
        GLES20.glVertexAttribPointer(positionHandle, positionDataSize, GLES20.GL_FLOAT, false,
                strideBytes, triangleBuffer);

        GLES20.glEnableVertexAttribArray(positionHandle);

        // Pass in the color information
        triangleBuffer.position(colorOffset);
        GLES20.glVertexAttribPointer(colorHandle, colorDataSize, GLES20.GL_FLOAT, false,
                strideBytes, triangleBuffer);

        GLES20.glEnableVertexAttribArray(colorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view)
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
