package com.reanstudio.imagefilter.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.reanstudio.imagefilter.shader.ReinierGraphicTools;
import com.reanstudio.imagefilter.shape.Sprite;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

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
    public static float uvs[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;

    // Our screen resolution
    float screenWidth = 1280;
    float screenHeight = 768;
    float ssu = 1.0f;
    float ssx = 1.0f;
    float ssy = 1.0f;
    float swp = 320.0f;
    float shp = 480.0f;

    private Context context;
    private long lastTime;
    private int program;

//    public Rect rect;
    public Sprite sprite;

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
        // Setup our scaling system
        setupScaling();
        // Create the triangle
        setupTriangle();
        // Create the image information
        setupImage();

        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Create the shaders
        int vertexShader = ReinierGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                ReinierGraphicTools.VS_SOLID_COLOR);
        int fragmentShader = ReinierGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                ReinierGraphicTools.FS_SOLID_COLOR);

        ReinierGraphicTools.SP_SOLID_COLOR = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(ReinierGraphicTools.SP_SOLID_COLOR, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(ReinierGraphicTools.SP_SOLID_COLOR, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(ReinierGraphicTools.SP_SOLID_COLOR);                  // creates OpenGL ES program executables

        // Create the shaders, images
        vertexShader = ReinierGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                ReinierGraphicTools.VS_IMAGE);
        fragmentShader = ReinierGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                ReinierGraphicTools.FS_IMAGE);

        ReinierGraphicTools.SP_IMAGE = GLES20.glCreateProgram();
        GLES20.glAttachShader(ReinierGraphicTools.SP_IMAGE, vertexShader);
        GLES20.glAttachShader(ReinierGraphicTools.SP_IMAGE, fragmentShader);
        GLES20.glLinkProgram(ReinierGraphicTools.SP_IMAGE);

        // Set our shader programm
        GLES20.glUseProgram(ReinierGraphicTools.SP_IMAGE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // We need to know the current width and height.
        screenWidth = width;
        screenHeight = height;

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) screenWidth, (int) screenHeight);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
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

        // setup our scaling system
        setupScaling();
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
//        updateSprite();

        // Render our example
        draw(trxProjectionAndView);

        // Save the current time to see how long it took :).
        lastTime = now;
    }

    private void draw(float[] m) {
        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(ReinierGraphicTools.SP_SOLID_COLOR, "vPosition");
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Get handle to texture coordinates location
        int texCoordLoc = GLES20.glGetAttribLocation(ReinierGraphicTools.SP_IMAGE, "a_texCoord");
        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(texCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);
        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(texCoordLoc);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(ReinierGraphicTools.SP_SOLID_COLOR, "uMVPMatrix");
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Get handle to texture locations
        int samplerLoc = GLES20.glGetUniformLocation(ReinierGraphicTools.SP_IMAGE, "s_texture");

        // Set the sampler texture unit to 0, where we have saved the texture
        GLES20.glUniform1i(samplerLoc, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(texCoordLoc);
    }

    public void setupTriangle() {
        // Initial rect
//        rect = new Rect();
//        rect.left = 10;
//        rect.right = 100;
//        rect.bottom = 100;
//        rect.top = 200;

//        sprite = new Sprite(ssu);
//        vertices = sprite.getTransformedVertices();

        // We have to create the vertices of our triangle.
//        vertices = new float[] {
//            10.0f, 200f, 0.0f,
//            10.0f, 100f, 0.0f,
//            100f, 100f, 0.0f,
//            100f, 200f, 0.0f,
//        };

        // The order of vertexrendering.
//        indices = new short[]{0, 1, 2, 0, 2, 3};

        // We will need a randomizer
        Random rnd = new Random();

        // Our collection of vertices
        vertices = new float[30*4*3];

        // Create the vertex data
        for(int i=0;i<30;i++)
        {
            int offset_x = rnd.nextInt((int)swp);
            int offset_y = rnd.nextInt((int)shp);

            // Create the 2D parts of our 3D vertices, others are default 0.0f
            vertices[(i*12) + 0] = offset_x;
            vertices[(i*12) + 1] = offset_y + (30.0f*ssu);
            vertices[(i*12) + 2] = 0f;
            vertices[(i*12) + 3] = offset_x;
            vertices[(i*12) + 4] = offset_y;
            vertices[(i*12) + 5] = 0f;
            vertices[(i*12) + 6] = offset_x + (30.0f*ssu);
            vertices[(i*12) + 7] = offset_y;
            vertices[(i*12) + 8] = 0f;
            vertices[(i*12) + 9] = offset_x + (30.0f*ssu);
            vertices[(i*12) + 10] = offset_y + (30.0f*ssu);
            vertices[(i*12) + 11] = 0f;
        }

        // The indices for all textured quads
        indices = new short[30*6];
        int last = 0;
        for(int i=0;i<30;i++)
        {
            // We need to set the new indices for the new quad
            indices[(i*6) + 0] = (short) (last + 0);
            indices[(i*6) + 1] = (short) (last + 1);
            indices[(i*6) + 2] = (short) (last + 2);
            indices[(i*6) + 3] = (short) (last + 0);
            indices[(i*6) + 4] = (short) (last + 2);
            indices[(i*6) + 5] = (short) (last + 3);

            // Our indices are connected to the vertices so we need to keep them
            // in the correct order.
            // normal quad = 0,1,2,0,2,3 so the next one will be 4,5,6,4,6,7
            last = last + 4;
        }

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

//    public void translateSprite() {
//        vertices = new float[] {
//            rect.left, rect.top, 0.0f,
//            rect.left, rect.bottom, 0.0f,
//            rect.right, rect.bottom, 0.0f,
//            rect.right, rect.top, 0.0f,
//        };
//
//        // The vertex buffer
//        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        vertexBuffer = bb.asFloatBuffer();
//        vertexBuffer.put(vertices);
//        vertexBuffer.position(0);
//    }

    public void setupImage() {
//        // Create our UV coordinates
//        uvs = new float[] {
//            0.0f, 0.0f,
//            0.0f, 1.0f,
//            1.0f, 1.0f,
//            1.0f, 0.0f,
//        };

        // We will use a randomizer for randomizing the textures from texture atlas.
        // This is strictly optional as it only effects the output of our app,
        // Not the actual knowledge.
        Random rnd = new Random();

        // 30 imageobjects times 4 vertices times (u and v)
        uvs = new float[30*4*2];

        // We will make 30 randomly textures objects
        for(int i=0; i<30; i++)
        {
            int random_u_offset = rnd.nextInt(2);
            int random_v_offset = rnd.nextInt(2);

            // Adding the UV's using the offsets
            uvs[(i*8) + 0] = random_u_offset * 0.5f;
            uvs[(i*8) + 1] = random_v_offset * 0.5f;
            uvs[(i*8) + 2] = random_u_offset * 0.5f;
            uvs[(i*8) + 3] = (random_v_offset+1) * 0.5f;
            uvs[(i*8) + 4] = (random_u_offset+1) * 0.5f;
            uvs[(i*8) + 5] = (random_v_offset+1) * 0.5f;
            uvs[(i*8) + 6] = (random_u_offset+1) * 0.5f;
            uvs[(i*8) + 7] = random_v_offset * 0.5f;
        }

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate textures, if more needed, alter these number
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);

        // Retrieve our image from resources
        int id = context.getResources().getIdentifier("drawable/textureatlas", null,
                context.getPackageName());

        // Temporary create a bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Set wrapping mode
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Load the bitmap into the bound texture
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // We are done using the bitmap so we should recycle it
        bitmap.recycle();
    }

    public void processTouchEvent(MotionEvent event) {
        // Get the half of screen value
        int screenahlf = (int) (screenWidth / 2);
        int screenheightpart = (int) (screenHeight / 3);
//        if (event.getX() < screenahlfX) {
//            rect.left -= 10;
//            rect.right -= 10;
//        } else {
//            rect.left += 10;
//            rect.right += 10;
//        }

//        if (event.getY() > screenhalfY) {
//            rect.top -= 10;
//            rect.bottom -= 10;
//        } else {
//            rect.top += 10;
//            rect.bottom += 10;
//        }

        // Update the new data
//        translateSprite();

//        if (event.getX() < screenahlf) {
//            // Left screen touch
//            if (event.getY() < screenheightpart) {
//                sprite.scale(-0.01f);
//            } else if (event.getY() < (screenheightpart * 2)) {
//                sprite.translate(-10f * ssu, -10f * ssu);
//            } else {
//                sprite.rotate(0.01f);
//            }
//        } else {
//            // Right screen touch
//            if (event.getY() < screenheightpart) {
//                sprite.scale(0.01f);
//            } else if (event.getY() < (screenheightpart * 2)) {
//                sprite.translate(10f * ssu, 10f * ssu);
//            } else {
//                sprite.rotate(-0.01f);
//            }
//        }
    }

    public void updateSprite() {
        // Get new transformed vertices
        vertices = sprite.getTransformedVertices();

        // The vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    public void setupScaling() {
        // The screen resolutions
        swp = (int) (context.getResources().getDisplayMetrics().widthPixels);
        shp = (int) (context.getResources().getDisplayMetrics().heightPixels);

        // Orientation is assumed potrait
        ssx = swp / 320.0f;
        ssy = shp / 480.0f;

        // Get our uniform scaler
        if (ssx > ssy) {
            ssu = ssy;
        } else {
            ssu = ssx;
        }
    }
}
