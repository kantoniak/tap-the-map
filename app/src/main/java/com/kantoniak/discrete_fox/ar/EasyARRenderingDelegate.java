package com.kantoniak.discrete_fox.ar;

import com.kantoniak.discrete_fox.scene.RenderingDelegate;

import cn.easyar.Engine;

/**
 * Class responsible for easy AR rendering delegate.
 */
public class EasyARRenderingDelegate implements RenderingDelegate {

    private final EasyARController easyArController;

    public EasyARRenderingDelegate(EasyARController easyArController) {
        this.easyArController = easyArController;
    }

    /**
     * When surface created.
     */
    @Override
    public void onSurfaceCreated() {
        synchronized (easyArController) {
            easyArController.initGL();
        }
    }

    /**
     * When surface changed.
     *
     * @param width  Width
     * @param height Height
     */
    @Override
    public void onSurfaceChanged(int width, int height) {
        synchronized (easyArController) {
            easyArController.resizeGL(width, height);
        }
    }

    /**
     * When after attached to window.
     */
    @Override
    public void onAfterAttachedToWindow() {
        synchronized (easyArController) {
            if (easyArController.initialize()) {
                easyArController.start();
            }
        }
    }

    /**
     * When before detached from window.
     */
    @Override
    public void onBeforeDetachedFromWindow() {
        synchronized (easyArController) {
            easyArController.stop();
            easyArController.dispose();
        }
    }

    /**
     * When resume.
     */
    @Override
    public void onResume() {
        Engine.onResume();
    }

    /**
     * When pause.
     */
    @Override
    public void onPause() {
        Engine.onPause();
    }
}
