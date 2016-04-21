package com.reanstudio.imagefilter.shader;

import android.opengl.GLES20;

/**
 * Created by yahyamukhlis on 4/19/16.
 */
public class ReinierGraphicTools {

    // Program variables
    public static int SP_SOLID_COLOR;
    public static int SP_IMAGE;

    // vertex shader
    public static final String VS_SOLID_COLOR =
            "uniform 	mat4 		uMVPMatrix;" +
            "attribute 	vec4 		vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    public static final String VS_IMAGE =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "}";

    // fragment shader
    public static final String FS_SOLID_COLOR =
            "precision mediump float;" +
            "void main() {" +
            "  gl_FragColor = vec4(0.5,0,0,1);" +
            "}";

    public static final String FS_IMAGE =
            "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D s_texture;" +
            "void main() {" +
            "  gl_FragColor = texture2D(s_texture, v_texCoord);" +
            "}";

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
