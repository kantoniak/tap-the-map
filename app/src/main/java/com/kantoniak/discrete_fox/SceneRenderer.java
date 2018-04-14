package com.kantoniak.discrete_fox;

import android.support.annotation.NonNull;

import com.kantoniak.discrete_fox.ar.ARController;
import com.kantoniak.discrete_fox.ar.ARSceneRenderer;
import com.kantoniak.discrete_fox.scene.Country;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SceneRenderer extends ARSceneRenderer {

    private final MainActivity mainActivity;

    // Scene elements
    private DirectionalLight directionalLight;
    List<String> countryCodes = Arrays.asList("at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "gb", "gr", "hr", "hu", "ie", "it", "lt", "lu", "lv", "ne", "pl", "pt", "ro", "se", "si", "sk");
    List<Country> countries = new LinkedList<>();

    public SceneRenderer(MainActivity mainActivity, ARController arController) {
        super(mainActivity, arController);
        this.mainActivity = mainActivity;
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
        for (String code : countryCodes) {
            Country country = new Country(code, 3, 0x81C784, 0x388E3C);
            country.loadObject(mainActivity, mTextureManager);
            country.registerObject(getCurrentScene(), objectPicker);
            countries.add(country);
        }
    }

    @Override
    protected void onRender(long elapsedRealtime, double deltaTime) {
        super.onRender(elapsedRealtime, deltaTime);
        updateCameraMatrices();
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        countries.forEach((Country country) -> {
            if (country.containsObject(object)) {
                country.onPicked();
            }
        });
    }

    @Override
    public void onNoObjectPicked() {

    }
}
