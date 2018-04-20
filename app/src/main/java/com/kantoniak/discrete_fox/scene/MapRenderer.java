package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

public class MapRenderer extends Renderer implements OnObjectPickedListener {

    private final ViewMatrixOverrideCamera camera;
    private ObjectColorPicker objectPicker;

    // Temporary
    private Cube cube;

    public MapRenderer(Context context) {
        super(context);
        this.camera = new ViewMatrixOverrideCamera();
    }

    @Override
    protected void initScene() {

        setupCamera();
        setupObjectPicker();

        cube = new Cube(0.5f);
        Material material = new Material();
        material.setColor(0xffff0000);
        cube.setMaterial(material);
        cube.setPosition(0,0,0.25f);
        getCurrentScene().addChild(cube);

        objectPicker.registerObject(cube);
    }

    protected void setupCamera() {
        camera.setNearPlane(0.2f);
        camera.setFarPlane(500.f);
        getCurrentScene().addAndSwitchCamera(camera);
    }

    public ViewMatrixOverrideCamera getCamera() {
        return camera;
    }

    private void setupObjectPicker() {
        objectPicker = new ObjectColorPicker(this);
        objectPicker.setOnObjectPickedListener(this);
    }

    @Override
    protected void onRender(long elapsed, double delta) {
        super.onRender(elapsed, delta);
        cube.rotate(Vector3.Axis.Z, 1.0);
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

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        if (object == cube) {
            if (cube.getScale().x > 1) {
                cube.setScale(1.f);
            } else {
                cube.setScale(2.f);
            }
        }
    }

    @Override
    public void onNoObjectPicked() {

    }
}
