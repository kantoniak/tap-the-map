package com.kantoniak.discrete_fox.scene;

import com.kantoniak.discrete_fox.new_ar.ARCameraController;

import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.scene.ASceneFrameCallback;

public class UpdateMatricesCallback extends ASceneFrameCallback {

    private final ARCameraController arCameraController;
    private final ViewMatrixOverrideCamera camera;

    public UpdateMatricesCallback(ARCameraController arCameraController, ViewMatrixOverrideCamera camera) {
        this.arCameraController = arCameraController;
        this.camera = camera;
    }

    @Override
    public boolean callPreFrame() {
        return true;
    }

    @Override
    public void onPreFrame(long sceneTime, double deltaTime) {
        synchronized (arCameraController) {
            if (arCameraController.getProjectionMatrix() == null) {
                return;
            }

            Matrix4 projectionMatrix = new Matrix4(arCameraController.getProjectionMatrix().data);
            Matrix4 viewMatrix = new Matrix4(arCameraController.getViewMatrix().data);

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

    @Override
    public void onPreDraw(long sceneTime, double deltaTime) {

    }

    @Override
    public void onPostFrame(long sceneTime, double deltaTime) {

    }
}
