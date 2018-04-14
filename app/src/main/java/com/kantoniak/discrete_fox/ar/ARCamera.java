package com.kantoniak.discrete_fox.ar;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Matrix4;

/**
 * Camera implementation for use with EasyAR. Overrides view matrix with matrix returned by EasyAR
 * engine.
 */
public class ARCamera extends Camera {

    private Matrix4 viewMatrixOverride = new Matrix4();

    public void setViewMatrixOverride(Matrix4 viewMatrixOverride) {
        this.viewMatrixOverride = viewMatrixOverride;
    }

    @Override
    public Matrix4 getViewMatrix() {
        return viewMatrixOverride;
    }
}
