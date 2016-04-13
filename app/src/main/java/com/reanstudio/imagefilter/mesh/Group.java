package com.reanstudio.imagefilter.mesh;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yahyamukhlis on 4/13/16.
 */
public class Group extends Mesh {

    private Vector<Mesh> child = new Vector<>();

    @Override
    public void draw(GL10 gl) {
        int size = child.size();
        for (int i = 0; i < size; i++) {
            child.get(i).draw(gl);
        }
    }

    public void add(int location, Mesh mesh) {
        child.add(location, mesh);
    }

    public boolean add(Mesh mesh) {
        return child.add(mesh);
    }

    public void clear() {
        child.clear();
    }

    public Mesh get(int location) {
        return child.get(location);
    }

    public Mesh remove(int location) {
        return child.remove(location);
    }

    public boolean remove(Object object) {
        return child.remove(object);
    }

    public int size() {
        return child.size();
    }
}
