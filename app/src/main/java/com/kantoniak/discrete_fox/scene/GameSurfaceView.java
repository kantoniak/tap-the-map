package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.util.AttributeSet;

import org.rajawali3d.view.SurfaceView;

public class GameSurfaceView extends SurfaceView {

    private ARRenderingDelegate arRenderingDelegate;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setArRenderingDelegate(ARRenderingDelegate arRenderingDelegate) {
        this.arRenderingDelegate = arRenderingDelegate;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (arRenderingDelegate != null) {
            arRenderingDelegate.onAfterAttachedToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (arRenderingDelegate != null) {
            arRenderingDelegate.onBeforeDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (arRenderingDelegate != null) {
            arRenderingDelegate.onResume();
        }
    }

    @Override
    public void onPause() {
        if (arRenderingDelegate != null) {
            arRenderingDelegate.onPause();
        }
        super.onPause();
    }
}
