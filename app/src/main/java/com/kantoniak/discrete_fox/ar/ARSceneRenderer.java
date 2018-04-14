package com.kantoniak.discrete_fox.ar;

import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Base class for renderers which use EasyAR.
 */
public abstract class ARSceneRenderer extends Renderer implements OnObjectPickedListener {

    // EasyAR
    protected final ARController arController; // private

    // Rajawali
    private final RenderBackgroundCallback renderBackgroundCallback;
    protected ARCamera arCamera;
    protected ObjectColorPicker objectPicker;

    public ARSceneRenderer(Context context, ARController arController) {
        super(context);
        setFrameRate(60);
        this.arController = arController;
        this.renderBackgroundCallback = new RenderBackgroundCallback(arController);
    }

    @Override
    protected void initScene() {
        getCurrentScene().registerFrameCallback(renderBackgroundCallback);
        this.setupColorPicker();
        this.setupCamera();
    }

    protected void setupColorPicker() {
        objectPicker = new ObjectColorPicker(this);
        objectPicker.setOnObjectPickedListener(this);
    }

    protected void setupCamera() {
        arCamera = new ARCamera();
        arCamera.setNearPlane(0.2f);
        arCamera.setFarPlane(500.f);

        getCurrentScene().addAndSwitchCamera(arCamera);
    }

    @Override
    public void onRenderSurfaceCreated(EGLConfig config, GL10 gl, int width, int height) {
        super.onRenderSurfaceCreated(config, gl, width, height);
        synchronized (arController) {
            arController.initGL();
        }
    }

    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        synchronized (arController) {
            arController.resizeGL(width, height);
        }
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            objectPicker.getObjectAt(event.getX(), event.getY());
        }
    }

    protected void updateCameraMatrices() {
        synchronized (arController) {
            // TODO(make boolean flag if changed)
            if (arController.getProjectionMatrix() == null) {
                return;
            }

            Matrix4 projectionMatrix = new Matrix4(arController.getProjectionMatrix().data);
            Matrix4 viewMatrix = new Matrix4(arController.getViewMatrix().data);

            Vector3 cameraPosition = new Vector3(
                    viewMatrix.getDoubleValues()[12],
                    viewMatrix.getDoubleValues()[13],
                    viewMatrix.getDoubleValues()[14]);

            arCamera.setProjectionMatrix(projectionMatrix);
            arCamera.setPosition(cameraPosition);
            arCamera.setRotation(viewMatrix);
            arCamera.setViewMatrixOverride(viewMatrix);
        }
    }
}
