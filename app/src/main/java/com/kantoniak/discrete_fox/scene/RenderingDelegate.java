package com.kantoniak.discrete_fox.scene;

public interface RenderingDelegate {
    void onSurfaceCreated();
    void onSurfaceChanged(int width, int height);

    void onAfterAttachedToWindow();
    void onBeforeDetachedFromWindow();

    void onResume();
    void onPause();
}
