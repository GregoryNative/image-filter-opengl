package com.reanstudio.imagefilter.shape;

/**
 * Created by yahyamukhlis on 4/22/16.
 */
public class TextObject {

    public String text;
    public float x;
    public float y;
    public float[] color;

    public TextObject() {
        text = "default";
        x = 0f;
        y = 0f;
        color = new float[] {1f, 1f, 1f, 1.0f};
    }

    public TextObject(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
        color = new float[] {1f, 1f, 1f, 1.0f};
    }
}
