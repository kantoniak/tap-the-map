package com.kantoniak.discrete_fox.ar;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Matrix4;

/**
 * Camera implementation for use with EasyAR. Overrides view matrix with matrix returned by EasyAR
 * engine.
 */
public class ViewMatrixOverrideCamera extends Camera {

    private Matrix4 viewMatrixOverride = new Matrix4();
    private Matrix4 viewMatrixOverrideScaled = new Matrix4();

    private static final float MAX_ZOOM = 4.f;
    private static final float MIN_ZOOM = 0.20f;
    private static final float ZOOM_STEP = 0.20f;
    private float currentZoom = 1.f;

    /**
     * Set view matrix override.
     */
    public void setViewMatrixOverride(Matrix4 viewMatrixOverride) {
        this.viewMatrixOverride = viewMatrixOverride;
        this.viewMatrixOverrideScaled = viewMatrixOverride.scale(5.f * currentZoom);
    }

    /**
     * Get view matrix.
     */
    @Override
    public Matrix4 getViewMatrix() {
        return viewMatrixOverrideScaled;
    }

    /**
     * Zoom in.
     */
    public void zoomIn() {
        currentZoom += ZOOM_STEP;
        if (currentZoom > MAX_ZOOM) {
            currentZoom = MAX_ZOOM;
        }
        viewMatrixOverrideScaled = viewMatrixOverride.scale(currentZoom);
    }

    /**
     * Zoom out.
     */
    public void zoomOut() {
        currentZoom -= ZOOM_STEP;
        if (currentZoom < MIN_ZOOM) {
            currentZoom = MIN_ZOOM;
        }
        viewMatrixOverrideScaled = viewMatrixOverride.scale(currentZoom);
    }
}
