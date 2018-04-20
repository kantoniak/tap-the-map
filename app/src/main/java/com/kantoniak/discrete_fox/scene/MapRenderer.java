package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

public class MapRenderer extends Renderer implements OnObjectPickedListener {

    private final Context context;
    private final Map map;
    private final ViewMatrixOverrideCamera camera;
    private ObjectColorPicker objectPicker;

    public MapRenderer(Context context, Map map) {
        super(context);
        this.context = context;
        this.camera = new ViewMatrixOverrideCamera();
        this.map = map;
    }

    @Override
    protected void initScene() {

        setupCamera();
        setupObjectPicker();

        Cube cube = new Cube(0.5f);
        Material material = new Material();
        material.setColor(0xffff0000);
        cube.setMaterial(material);
        cube.setPosition(0,0,0.25f);
        getCurrentScene().addChild(cube);

        //TODO(kantoniak): Move country loading out of Country and renderer?
        // Countries
        for (String code : Map.COUNTRY_CODES) {
            Country country = new Country(code, 3, 0xFF81C784, 0xFF388E3C, map.getCountryMiddle(code));
            country.loadObject(context, mTextureManager);
            country.registerObject(getCurrentScene(), objectPicker);
            map.addCountry(country);
        }
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
        for (java.util.Map.Entry<String, Country> entry: map.getCountries().entrySet()) {
            if (entry.getValue().containsObject(object)) {
                entry.getValue().onPicked();
            }
        }
    }

    @Override
    public void onNoObjectPicked() {
        // Do nothing
    }
}
