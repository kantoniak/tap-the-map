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
    private boolean disabled;

    private Object3D baseObject;
    private Material baseMaterial;

    private Object3D topObject;
    private Material topMaterial;

    private static int DEFAULT_COLOR = 0xE0E0E0;
    private static int DISABLED_COLOR = 0x9E9E9E;
    private static int BLACK_COLOR = 0x000000;
    private int minColor;
    private int maxColor;

    private static float TOP_HEIGHT = 0.01f;
    private static float NEAR_ZERO_HEIGHT = 0.001f;

    public Country(String code, int maxHeight, int minColor, int maxColor) {
        this.code = code;
        this.maxHeight = maxHeight;
        this.minColor = minColor;
        this.maxColor = maxColor;
        this.disabled = true;
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

        topObject = baseObject.clone();
        topObject.setMaterial(topMaterial);
        topObject.setScaleZ(TOP_HEIGHT);

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
        if (disabled) {
            return;
        }

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

    private void zeroChoice() {
        height = 0;
        baseObject.setScaleZ(NEAR_ZERO_HEIGHT);
        topObject.setZ(NEAR_ZERO_HEIGHT);
        if (disabled) {
            baseMaterial.setColor(DISABLED_COLOR);
            topMaterial.setColor(DISABLED_COLOR);
        } else {
            baseMaterial.setColor(DEFAULT_COLOR);
            topMaterial.setColor(DEFAULT_COLOR);
        }
    }

    private int getBaseColor(int topColor) {
        return ColorUtils.blendARGB(BLACK_COLOR, topColor, 0.5f);
    }

    public int getHeight() {
        return height;
    }

    public String getCode() {
        return code;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (disabled) {
            zeroChoice();
        } else {
            baseMaterial.setColor(DEFAULT_COLOR);
            topMaterial.setColor(DEFAULT_COLOR);
        }
    }

    public void setColors(int minColor, int maxColor) {
        this.minColor = minColor;
        this.maxColor = maxColor;
    }
}
