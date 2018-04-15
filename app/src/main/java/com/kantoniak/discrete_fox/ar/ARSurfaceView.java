package com.kantoniak.discrete_fox.ar;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.kantoniak.discrete_fox.MainActivity;
import com.kantoniak.discrete_fox.SceneRenderer;
import com.kantoniak.discrete_fox.scene.Map;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import cn.easyar.Engine;

public class ARSurfaceView extends SurfaceView {

    private final ARController arController;
    private final ARSceneRenderer sceneRenderer;

    public ARSurfaceView(Context context, Map map) {
        super(context);
        arController = new ARController();
        sceneRenderer = new SceneRenderer(context, arController, map);

        this.setFrameRate(60.0);
        this.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);
        this.setZOrderMediaOverlay(true);
        this.setSurfaceRenderer(sceneRenderer);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        synchronized (arController) {
            if (arController.initialize()) {
                arController.start();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        synchronized (arController) {
            arController.stop();
            arController.dispose();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onResume() {
        super.onResume();
        Engine.onResume();
    }

    @Override
    public void onPause() {
        Engine.onPause();
        super.onPause();
    }

    public ARSceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }
}
