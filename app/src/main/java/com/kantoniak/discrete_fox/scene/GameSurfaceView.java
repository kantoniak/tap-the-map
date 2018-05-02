package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.util.AttributeSet;

import org.rajawali3d.view.SurfaceView;

public class GameSurfaceView extends SurfaceView {

    private RenderingDelegate renderingDelegate;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderingDelegate(RenderingDelegate renderingDelegate) {
        this.renderingDelegate = renderingDelegate;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (renderingDelegate != null) {
            renderingDelegate.onAfterAttachedToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (renderingDelegate != null) {
            renderingDelegate.onBeforeDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (renderingDelegate != null) {
            renderingDelegate.onResume();
        }
    }

    @Override
    public void onPause() {
        if (renderingDelegate != null) {
            renderingDelegate.onPause();
        }
        super.onPause();
    }
}
