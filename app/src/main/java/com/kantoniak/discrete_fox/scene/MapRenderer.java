package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.renderer.Renderer;

public class MapRenderer extends Renderer {

    private final ViewMatrixOverrideCamera camera = new ViewMatrixOverrideCamera();

    private Cube cube;

    public MapRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {

        cube = new Cube(0.5f);
        Material material = new Material();
        material.setColor(0xffff0000);
        cube.setMaterial(material);
        cube.setPosition(0,0,0.25f);
        getCurrentScene().addChild(cube);

        setupCamera();
    }

    protected void setupCamera() {
        camera.setNearPlane(0.2f);
        camera.setFarPlane(500.f);

        getCurrentScene().addAndSwitchCamera(camera);
    }

    @Override
    protected void onRender(long elapsedRealtime, double deltaTime) {
        super.onRender(elapsedRealtime, deltaTime);
        cube.rotate(Vector3.Axis.Z, 1.0);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    public ViewMatrixOverrideCamera getCamera() {
        return camera;
    }
}
