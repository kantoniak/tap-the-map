package com.kantoniak.discrete_fox;

import android.support.annotation.NonNull;

import com.kantoniak.discrete_fox.ar.ARController;
import com.kantoniak.discrete_fox.ar.ARSceneRenderer;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;

public class SceneRenderer extends ARSceneRenderer {

    private final MainActivity mainActivity;

    // Scene elements
    private Cube cube;
    private DirectionalLight directionalLight;

    public SceneRenderer(MainActivity mainActivity, ARController arController) {
        super(mainActivity, arController);
        this.mainActivity = mainActivity;
    }

    @Override
    protected void initScene() {
        super.initScene();

        // Lighting
        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        directionalLight.shouldUseObjectTransform(false);
        getCurrentScene().addLight(directionalLight);

        cube = new Cube(0.5f);
        Material cubeMaterial = new Material();
        cubeMaterial.setColor(0xff0000);
        cubeMaterial.enableLighting(true);
        cubeMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        cubeMaterial.setSpecularMethod(new SpecularMethod.Phong());
        cube.setMaterial(cubeMaterial);
        cube.setPosition(new Vector3());
        cube.setRotation(new Vector3());
        cube.setPosition(0,0,0.25f);
        cube.setDoubleSided(true);
        getCurrentScene().addChild(cube);
        objectPicker.registerObject(cube);

    }

    @Override
    protected void onRender(long elapsedRealtime, double deltaTime) {
        super.onRender(elapsedRealtime, deltaTime);
        updateCameraMatrices();

        cube.rotate(Vector3.Axis.Z, 1.0);
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        if (object == cube) {
            if (cube.getScaleZ() > 1) {
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
