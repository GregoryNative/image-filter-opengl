package com.reanstudio.imagefilter.shader;

import android.opengl.GLES20;

/**
 * Created by yahyamukhlis on 4/19/16.
 */
public class ReinierGraphicTools {

    // Program variables
    public static int SP_SOLID_COLOR;

    // vertex shader
    public static final String VS_SOLID_COLOR =
            "uniform 	mat4 		uMVPMatrix;" +
            "attribute 	vec4 		vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    // fragment shader
    public static final String FS_SOLID_COLOR =
            "precision mediump float;" +
            "void main() {" +
            "  gl_FragColor = vec4(0.5,0,0,1);" +
            "}";

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
