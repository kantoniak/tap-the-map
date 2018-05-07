package com.kantoniak.discrete_fox.scene;

/**
 * Rendering delegate.
 */
public interface RenderingDelegate {
    /**
     * When surface is created.
     */
    void onSurfaceCreated();

    /**
     * When surface has changed.
     * @param width width
     * @param height height
     */
    void onSurfaceChanged(int width, int height);

    /**
     * When after attached to window.
     */
    void onAfterAttachedToWindow();

    /**
     * When before detached from window.
     */
    void onBeforeDetachedFromWindow();

    /**
     * When resume.
     */
    void onResume();

    /**
     * When pause.
     */
    void onPause();
}
