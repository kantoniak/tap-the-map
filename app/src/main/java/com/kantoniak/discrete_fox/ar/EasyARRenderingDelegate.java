package com.kantoniak.discrete_fox.ar;

import com.kantoniak.discrete_fox.scene.RenderingDelegate;

import cn.easyar.Engine;

public class EasyARRenderingDelegate implements RenderingDelegate {

    private final EasyARController easyArController;
    private final ViewMatrixOverrideCamera camera;

    public EasyARRenderingDelegate(EasyARController easyArController, ViewMatrixOverrideCamera camera) {
        this.easyArController = easyArController;
        this.camera = camera;
    }

    @Override
    public void onSurfaceCreated() {
        synchronized (easyArController) {
            easyArController.initGL();
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        synchronized (easyArController) {
            easyArController.resizeGL(width, height);
        }
    }

    @Override
    public void onAfterAttachedToWindow() {
        synchronized (easyArController) {
            if (easyArController.initialize()) {
                easyArController.start();
            }
        }
    }

    @Override
    public void onBeforeDetachedFromWindow() {
        synchronized (easyArController) {
            easyArController.stop();
            easyArController.dispose();
        }
    }

    @Override
    public void onResume() {
        Engine.onResume();
    }

    @Override
    public void onPause() {
        Engine.onPause();
    }
}
