package pl.gov.stat.tapthemap.ar;

import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.scene.ASceneFrameCallback;

import pl.gov.stat.tapthemap.scene.MapRenderer;

/**
 * Class responsible for background update and matrices callback.
 */
public class UpdateBackgroundAndMatricesCallback extends ASceneFrameCallback {

    private final EasyARController arController;
    private final MapRenderer mRenderer;
    private final ViewMatrixOverrideCamera camera;
    private final boolean mapVertical;

    public UpdateBackgroundAndMatricesCallback(EasyARController arController, MapRenderer renderer, ViewMatrixOverrideCamera camera, boolean mapVertical) {
        this.arController = arController;
        this.mRenderer = renderer;
        this.camera = camera;
        this.mapVertical = mapVertical;
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
            if (mapVertical) {
                viewMatrix.rotate(Vector3.Axis.X, -90);
            }

            Vector3 cameraPosition = new Vector3(
                    viewMatrix.getDoubleValues()[12],
                    viewMatrix.getDoubleValues()[13],
                    viewMatrix.getDoubleValues()[14]);

            camera.setProjectionMatrix(projectionMatrix);
            camera.setPosition(cameraPosition);
            camera.setRotation(viewMatrix);
            camera.setViewMatrixOverride(viewMatrix);

            // Top 3x3 part of this 4x4 matrix is rotation.
            double[] rotatorData = new Matrix4(arController.getViewMatrix().data).transpose().getDoubleValues();

            // See the article about matrix decomposition (http://nghiaho.com/?page_id=846)
            //double rotAroundTop = 180 + Math.toDegrees(Math.atan2(rotatorData[4], rotatorData[0]));
            double rotAroundRight = Math.toDegrees(Math.atan2(rotatorData[9], rotatorData[10])) + 90 + (mapVertical ? 90 : 0);
            //double rotAroundDepth = -Math.toDegrees(Math.atan2(rotatorData[8], Math.sqrt(rotatorData[4]*rotatorData[4] + rotatorData[0]*rotatorData[0])));

            Quaternion testRot4 = new Quaternion().fromEuler(0, rotAroundRight, 0);
            mRenderer.getMap().getCountries().forEach((country, instance) -> instance.setNamePlateRotation(testRot4));
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

    private static String matrixToString(Matrix4 matrix) {
        double[] data = matrix.getDoubleValues();
        return "[" + data[0] + ", " + data[1] + ", " + data[2] + ", " + data[3] + "; "
                + data[4] + ", " + data[5] + ", " + data[6] + ", " + data[7] + "; "
                + data[8] + ", " + data[9] + ", " + data[10] + ", " + data[11] + "; "
                + data[12] + ", " + data[13] + ", " + data[14] + ", " + data[15] + "]";
    }
}
