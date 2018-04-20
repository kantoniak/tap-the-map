package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MapRenderer extends Renderer implements OnObjectPickedListener {

    private final Context context;
    private final Map map;
    private final Camera camera;
    private ARRenderingDelegate arRenderingDelegate;
    private ObjectColorPicker objectPicker;

    public MapRenderer(Context context, Map map, Camera camera) {
        super(context);
        this.context = context;
        this.camera = camera;
        this.map = map;
    }

    public void setArRenderingDelegate(ARRenderingDelegate arRenderingDelegate) {
        this.arRenderingDelegate = arRenderingDelegate;
    }

    @Override
    public void onRenderSurfaceCreated(EGLConfig config, GL10 gl, int width, int height) {
        super.onRenderSurfaceCreated(config, gl, width, height);
        if (arRenderingDelegate != null) {
            arRenderingDelegate.onSurfaceCreated();
        }
    }

    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        if (arRenderingDelegate != null) {
            arRenderingDelegate.onSurfaceChanged(width, height);
        }
    }

    @Override
    protected void initScene() {

        getCurrentScene().addAndSwitchCamera(camera);
        setupObjectPicker();

        //TODO(kantoniak): Move country loading out of Country and renderer?
        // Countries
        for (String code : Map.COUNTRY_CODES) {
            Country country = new Country(code, 3, 0xFF81C784, 0xFF388E3C, map.getCountryMiddle(code));
            country.loadObject(context, mTextureManager);
            country.registerObject(getCurrentScene(), objectPicker);
            map.addCountry(country);
        }
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
