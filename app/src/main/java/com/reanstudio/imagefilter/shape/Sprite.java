package com.reanstudio.imagefilter.shape;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by yahyamukhlis on 4/21/16.
 */
public class Sprite {

    private float angle;
    private float scale;
    private RectF base;
    private PointF translation;

    public Sprite(float ssu) {
        // Initialise our initial size around the 0,0 point
        base = new RectF(-50f * ssu, 50f * ssu, 50f * ssu, -50f * ssu);

        // Initial translation
        translation = new PointF(50f * ssu, 50f * ssu);

        // We start with our initial size
        scale = 1f;

        // We start in our initial angle
        angle = 0f;
    }

    public void translate(float deltax, float deltay) {
        // Update our location
        translation.x += deltax;
        translation.y += deltay;
    }

    public void scale(float deltas) {
        scale += deltas;
    }

    public void rotate(float deltaa) {
        angle += deltaa;
    }

    public float[] getTransformedVertices() {
        // Start with scaling
        float x1 = base.left * scale;
        float x2 = base.right * scale;
        float y1 = base.bottom * scale;
        float y2 = base.top * scale;

        // we now detach from our Rect because when rotating,
        // we need the separate points, so we do so in opengl order
        PointF one = new PointF(x1, y2);
        PointF two = new PointF(x1, y1);
        PointF three = new PointF(x2, y1);
        PointF four = new PointF(x2, y2);

        // We create the sin and cos function once, so we do not have calculate them each time
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        // Then we rotate each point
        one.x = x1 * c - y2 * s;
        one.y = x1 * s + y2 * c;
        two.x = x1 * c - y1 * s;
        two.y = x1 * s + y1 * c;
        three.x = x2 * c - y1 * s;
        three.y = x2 * s + y1 * c;
        four.x = x2 * c - y2 * s;
        four.y = x2 * s + y2 * c;

        // Finally we translate the sprite to its correct position
        one.x += translation.x;
        one.y += translation.y;
        two.x += translation.x;
        two.y += translation.y;
        three.x += translation.x;
        three.y += translation.y;
        four.x += translation.x;
        four.y += translation.y;

        // We now return our float array of vertices
        return new float[] {
            one.x, one.y, 0.0f,
            two.x, two.y, 0.0f,
            three.x, three.y, 0.0f,
            four.x, four.y, 0.0f,
        };
    }
}
