package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector2;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.scene.Scene;
import org.rajawali3d.util.ObjectColorPicker;

/**
 * Represents a single country on the map.
 *
 * About 3D rendering: world map exists in a plane where X is to the right and Z is to top of the
 * map. Y axis is perpendicular to the map.
 */
public class Country {

    private static int COLOR_DEFAULT = 0xFFE0E0E0;
    private static int COLOR_DISABLED = 0xFF9E9E9E;
    private static int COLOR_BLACK = 0xFF000000;

    /** Height ob the imported OBJ model */
    private static float BASE_HEIGHT = 0.1f;

    private static float Y_SCALE_MULTIPLIER = 0.5f;
    private static float NAME_Y_TRANSLATION = 0.1f;
    private static float NAME_SCALE = 0.1f;

    // State
    private final String code;
    private final int maxHeight;
    private int height = 0;
    private boolean disabled;

    // Colors
    private int minColor = COLOR_DEFAULT;
    private int maxColor = COLOR_DEFAULT;

    // 3D
    private Object3D countryBase;
    private Object3D countryTop;
    private Plane countryName;
    private Material countryBaseMaterial = new Material();
    private Material countryTopMaterial = new Material();
    private final Vector2 countryMiddle;

    public Country(String code, Vector2 countryMiddle) {
        this.code = code;
        this.maxHeight = 3;
        this.disabled = true;
        this.countryMiddle = countryMiddle;
    }

    public void createObject(AssetLoader loader) {
        countryBase = loader.loadObj("country_base_" + code + "_obj");
        countryTop = loader.loadObj("country_top_" + code + "_obj");

        countryBase.setDoubleSided(true);
        countryBaseMaterial.setColor(getBaseColor(COLOR_DEFAULT));
        countryBase.setMaterial(countryBaseMaterial);

        countryTop.setDoubleSided(true);
        countryTopMaterial.setColor(COLOR_DEFAULT);
        countryTop.setMaterial(countryTopMaterial);

        countryName = new Plane();
        countryName.setDoubleSided(true);
        countryName.setScale(NAME_SCALE);
        countryName.setX(countryMiddle.getX());
        countryName.setZ(-countryMiddle.getY());

        try {
            Material countryNameMaterial = new Material();
            countryNameMaterial.setColor(0x333333);
            countryNameMaterial.addTexture(loader.loadTexture("country_" + code + "_plate"));
            countryName.setMaterial(countryNameMaterial);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

        resetState();
    }

    public void resetState() {
        height = 0;
        disabled = true;
        updateVisuals();
    }

    public void registerObject(Scene scene, ObjectColorPicker objectPicker) {
        scene.addChild(countryBase);
        scene.addChild(countryTop);
        scene.addChild(countryName);

        objectPicker.registerObject(countryBase);
        objectPicker.registerObject(countryTop);
        objectPicker.registerObject(countryName);
    }

    public boolean containsObject(final Object3D object) {
        return countryBase == object || countryTop == object || countryName == object;
    }

    public void setHeight(int height) {
        this.height = height;
        if (this.height > maxHeight) {
            this.height = 0;
        }
        updateVisuals();
    }

    public void onPicked() {
        if (disabled) {
            return;
        }
        setHeight(++height);
        updateVisuals();

        // TODO(kedzior) mp3 play height
        //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.);
        //mp.start();
    }

    public void updateVisuals() {

        countryBase.setVisible(this.height > 0);
        countryName.setVisible(!this.disabled);

        if (height == 0) {
            countryTop.setPosition(new Vector3(0, 0, 0));
            countryTopMaterial.setColor(COLOR_DEFAULT);
            countryName.setY(NAME_Y_TRANSLATION);
        } else {
            countryBase.setScaleY(height * Y_SCALE_MULTIPLIER);
            countryTop.setPosition(new Vector3(0, BASE_HEIGHT * (float) height * Y_SCALE_MULTIPLIER, 0));
            countryName.setY(BASE_HEIGHT * (float) height * Y_SCALE_MULTIPLIER + NAME_Y_TRANSLATION);

            float colorRatio = (height - 1) / (float)(maxHeight - 1);
            countryBaseMaterial.setColor(getBaseColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio)));
            countryTopMaterial.setColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio));
        }

        if (disabled) {
            countryBaseMaterial.setColor(getBaseColor(COLOR_DISABLED));
            countryTopMaterial.setColor(COLOR_DISABLED);
        }

    }

    private int getBaseColor(int topColor) {
        return ColorUtils.blendARGB(COLOR_BLACK, topColor, 0.5f);
    }

    public int getHeight() {
        return height;
    }

    public String getCode() {
        return code;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        updateVisuals();
    }

    public void setColors(int minColor, int maxColor) {
        this.minColor = minColor;
        this.maxColor = maxColor;
        updateVisuals();
    }

}
