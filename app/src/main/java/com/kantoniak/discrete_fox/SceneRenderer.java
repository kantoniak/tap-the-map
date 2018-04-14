package com.kantoniak.discrete_fox;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kantoniak.discrete_fox.ar.ARController;
import com.kantoniak.discrete_fox.ar.ARSceneRenderer;
import com.kantoniak.discrete_fox.scene.Country;
import com.kantoniak.discrete_fox.scene.Map;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;

public class SceneRenderer extends ARSceneRenderer {

    private final Context context;

    // Scene elements
    private DirectionalLight directionalLight;
    private final Map map;

    public SceneRenderer(Context context, ARController arController, Map map) {
        super(context, arController);
        this.context = context;
        this.map = map;
    }

    @Override
    protected void initScene() {
        super.initScene();

        // Lighting
        directionalLight = new DirectionalLight(0f, 0f, 1f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(4);
        directionalLight.shouldUseObjectTransform(false);
        //getCurrentScene().addLight(directionalLight);

        // Countries
        for (String code : Map.COUNTRY_CODES) {
            Country country = new Country(code, 3, 0x81C784, 0x388E3C);
            country.loadObject(context, mTextureManager);
            country.registerObject(getCurrentScene(), objectPicker);
            map.addCountry(country);
        }
    }

    @Override
    protected void onRender(long elapsedRealtime, double deltaTime) {
        super.onRender(elapsedRealtime, deltaTime);
        updateCameraMatrices();
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        map.getCountries().forEach((Country country) -> {
            if (country.containsObject(object)) {
                country.onPicked();
            }
        });
    }

    @Override
    public void onNoObjectPicked() {

    }
}
