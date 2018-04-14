package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.scene.Scene;
import org.rajawali3d.util.ObjectColorPicker;

public class Country {

    private final String code;
    private final int maxHeight;
    private int height = 0;

    private Object3D baseObject;
    private Material baseMaterial;

    private Object3D topObject;
    private Material topMaterial;

    private static int DEFAULT_COLOR = 0xE0E0E0;
    private final int minColor;
    private final int maxColor;

    public Country(String code, int maxHeight, int minColor, int maxColor) {
        this.code = code;
        this.maxHeight = maxHeight;
        this.minColor = minColor;
        this.maxColor = maxColor;
    }

    public void loadObject(Context context, TextureManager textureManager) {
        int objFileId = context.getResources().getIdentifier("country_" + code + "_obj", "raw", context.getPackageName());
        LoaderOBJ loader = new LoaderOBJ(context.getResources(), textureManager, objFileId);

        try {
            loader.parse();
        } catch (ParsingException e) {
            Log.e("GAME", "OBJ load failed", e);
        }

        baseObject = loader.getParsedObject();
        baseObject.rotate(Vector3.Axis.X, -90);
        baseObject.setScaleX(-1.f);
        baseObject.setDoubleSided(true);

        setupMaterials();

        baseObject.setMaterial(baseMaterial);
        baseObject.setScaleZ(0.001f);

        topObject = baseObject.clone();
        topObject.setMaterial(topMaterial);
        topObject.setScaleZ(0.001f);

        zeroChoice();
    }

    private void setupMaterials() {
        baseMaterial = new Material();
        topMaterial = new Material();
    }

    public void registerObject(Scene scene, ObjectColorPicker objectPicker) {
        scene.addChild(baseObject);
        scene.addChild(topObject);
        objectPicker.registerObject(baseObject);
        objectPicker.registerObject(topObject);
    }

    public boolean containsObject(final Object3D object) {
        return baseObject == object || topObject == object;
    }

    public void onPicked() {
        nextState();
    }

    private void zeroChoice() {
        height = 0;
        baseObject.setScaleZ(0.001f);
        topObject.setZ(0.001f);
        baseMaterial.setColor(getBaseColor(DEFAULT_COLOR));
        topMaterial.setColor(DEFAULT_COLOR);
    }

    public void nextState() {
        height++;

        if (height > maxHeight) {
            zeroChoice();
            return;
        }

        baseObject.setScaleZ(0.5f * height);
        topObject.setZ(0.5f * baseObject.getScaleZ());
        float colorRatio = (height - 1) / (float)(maxHeight - 1);
        baseMaterial.setColor(getBaseColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio)));
        topMaterial.setColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio));
    }

    private int getBaseColor(int topColor) {
        return ColorUtils.blendARGB(0x000000, topColor, 0.5f);
    }

    public int getHeight() {
        return height;
    }

    public String getCode() {
        return code;
    }
}
