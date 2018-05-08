package com.kantoniak.discrete_fox.ar;

import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.scene.ASceneFrameCallback;

/**
 * Class responsible for background update and matrices callback.
 */
public class UpdateBackgroundAndMatricesCallback extends ASceneFrameCallback {

    private final EasyARController arController;
    private final ViewMatrixOverrideCamera camera;

    public UpdateBackgroundAndMatricesCallback(EasyARController arController, ViewMatrixOverrideCamera camera) {
        this.arController = arController;
        this.camera = camera;
    }

    /**
     * Call pre frame.
     *
     * @return True
     */
    @Override
    public boolean callPreFrame() {
        return true;
    }

    /**
     * When pre frame.
     */
    @Override
    public void onPreFrame(long sceneTime, double deltaTime) {
        synchronized (arController) {
            arController.render();

            if (arController.getProjectionMatrix() == null) {
                return;
            }

            Matrix4 projectionMatrix = new Matrix4(arController.getProjectionMatrix().data);
            Matrix4 viewMatrix = new Matrix4(arController.getViewMatrix().data)
                    .scale(-1.f, 1.f, 1.f)
                    .rotate(Vector3.Axis.X, -90);

            Vector3 cameraPosition = new Vector3(
                    viewMatrix.getDoubleValues()[12],
                    viewMatrix.getDoubleValues()[13],
                    viewMatrix.getDoubleValues()[14]);

            camera.setProjectionMatrix(projectionMatrix);
            camera.setPosition(cameraPosition);
            camera.setRotation(viewMatrix);
            camera.setViewMatrixOverride(viewMatrix);
        }
    }

    /**
     * When pre draw.
     */
    @Override
    public void onPreDraw(long sceneTime, double deltaTime) {

    }

    /**
     * When post frame.
     */
    @Override
    public void onPostFrame(long sceneTime, double deltaTime) {

    }
}
