package pl.gov.stat.tapthemap.scene;

import android.content.Context;
import android.util.AttributeSet;

import org.rajawali3d.view.SurfaceView;

/**
 * Class responsible for game surface view.
 */
public class GameSurfaceView extends SurfaceView {

    private RenderingDelegate renderingDelegate;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set rendering delegate.
     */
    public void setRenderingDelegate(RenderingDelegate renderingDelegate) {
        this.renderingDelegate = renderingDelegate;
    }

    /**
     * When attached to window.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (renderingDelegate != null) {
            renderingDelegate.onAfterAttachedToWindow();
        }
    }

    /**
     * When detached from window.
     */
    @Override
    protected void onDetachedFromWindow() {
        if (renderingDelegate != null) {
            renderingDelegate.onBeforeDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    /**
     * When resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (renderingDelegate != null) {
            renderingDelegate.onResume();
        }
    }

    /**
     * When paused.
     */
    @Override
    public void onPause() {
        if (renderingDelegate != null) {
            renderingDelegate.onPause();
        }
        super.onPause();
    }
}
