package com.kantoniak.discrete_fox.ar;

import org.rajawali3d.scene.ASceneFrameCallback;

class RenderBackgroundCallback extends ASceneFrameCallback {

    private final ARController arController;

    public RenderBackgroundCallback(ARController arController) {
        this.arController = arController;
    }

    @Override
    public boolean callPreFrame() {
        return true;
    }

    @Override
    public void onPreFrame(long sceneTime, double deltaTime) {
        synchronized (arController) {
            arController.onRender();
        }
    }

    @Override
    public void onPreDraw(long sceneTime, double deltaTime) {

    }

    @Override
    public void onPostFrame(long sceneTime, double deltaTime) {

    }
}
