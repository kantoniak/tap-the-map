package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;

import com.kantoniak.discrete_fox.country.Country;

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
public class CountryInstance {

    private static int COLOR_DEFAULT = 0xFFFAFAFA;
    private static int COLOR_DISABLED = 0xFFF5F5F5;
    private static int COLOR_BLACK = 0xFF000000;

    /** Height ob the imported OBJ model */
    private static float BASE_HEIGHT = 0.1f;

    private static float Y_SCALE_MULTIPLIER = 0.5f;
    private static float NAME_Y_TRANSLATION = 0.1f;
    private static float NAME_SCALE = 0.1f;

    // State
    private final Country country;
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

    public CountryInstance(Country country, Vector2 countryMiddle) {
        this.country = country;
        this.maxHeight = 3;
        this.disabled = true;
        this.countryMiddle = countryMiddle;
    }

    public void createObject(AssetLoader loader, Vector3 worldOffset) {
        countryBase = loader.loadCountryBase(country);
        countryTop = loader.loadCountryTop(country);

        countryBase.setDoubleSided(true);
        countryBaseMaterial.setColor(getBaseColor(COLOR_DEFAULT));
        countryBase.setMaterial(countryBaseMaterial);
        countryBase.setPosition(worldOffset);

        countryTop.setDoubleSided(true);
        countryTopMaterial.setColor(COLOR_DEFAULT);
        countryTop.setMaterial(countryTopMaterial);
        countryTop.setPosition(worldOffset);

        countryName = new Plane();
        countryName.setDoubleSided(true);
        countryName.setScale(NAME_SCALE);
        countryName.setX(countryMiddle.getX() + worldOffset.x);
        countryName.setZ(-countryMiddle.getY() + worldOffset.z);

        try {
            Material countryNameMaterial = new Material();
            countryNameMaterial.setColor(0x333333);
            countryNameMaterial.addTexture(loader.loadCountryNameTexture(country));
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
            countryTop.setY(0);
            countryTopMaterial.setColor(COLOR_DEFAULT);
            countryName.setY(NAME_Y_TRANSLATION);
        } else {
            countryBase.setScaleY(height * Y_SCALE_MULTIPLIER);
            countryTop.setY(BASE_HEIGHT * (float) height * Y_SCALE_MULTIPLIER);
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

    public Country getCountry() {
        return country;
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
