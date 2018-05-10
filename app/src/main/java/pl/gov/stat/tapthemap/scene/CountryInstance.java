package pl.gov.stat.tapthemap.scene;

import android.support.v4.graphics.ColorUtils;

import pl.gov.stat.tapthemap.Country;
import pl.gov.stat.tapthemap.gameplay.Gameplay;

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
 * <p>
 * About 3D rendering: world map exists in a plane where X is to the right and Z is to top of the
 * map. Y axis is perpendicular to the map.
 */
public class CountryInstance {

    private static int COLOR_DEFAULT = 0xFFFAFAFA;
    private static int COLOR_DISABLED = 0xFFF0F0F0;
    private static int COLOR_BLACK = 0xFF000000;

    private static float BASE_HEIGHT = 0.1f;
    private static float Y_SCALE_MULTIPLIER = 0.5f;
    private static float NAME_Y_TRANSLATION = 0.1f;
    private static float NAME_SCALE = 0.1f;

    // State
    private final Country country;
    private int height = 0;
    private boolean disabled;
    private boolean visible = true;

    // Position
    private Vector3 worldOffset;
    private Vector2 countryMiddle;

    // Colors
    private int minColor = COLOR_DEFAULT;
    private int maxColor = COLOR_DEFAULT;

    // 3D
    private Object3D countryBase;
    private Object3D countryTop;
    private Plane countryName;
    private Material countryBaseMaterial = new Material();
    private Material countryTopMaterial = new Material();

    public CountryInstance(Country country) {
        this.country = country;
        this.disabled = true;
    }

    /**
     * Initialize meshes.
     */
    public void initMeshes(Object3D countryBase, Object3D countryTop, ATexture countryNameTexture) {
        this.countryBase = countryBase;
        this.countryTop = countryTop;

        countryBase.setDoubleSided(true);
        countryBaseMaterial.setColor(getBaseColor(COLOR_DEFAULT));
        countryBase.setMaterial(countryBaseMaterial);

        countryTop.setDoubleSided(true);
        countryTopMaterial.setColor(COLOR_DEFAULT);
        countryTop.setMaterial(countryTopMaterial);

        countryName = new Plane();
        countryName.setDoubleSided(true);
        countryName.setScale(NAME_SCALE);

        try {
            Material countryNameMaterial = new Material();
            countryNameMaterial.setColor(0x333333);
            countryNameMaterial.addTexture(TextureManager.getInstance().addTexture(countryNameTexture));
            countryName.setMaterial(countryNameMaterial);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize positions.
     */
    public void initPositions(Vector3 worldOffset, Vector2 countryMiddle) {
        this.worldOffset = worldOffset;
        this.countryMiddle = countryMiddle;
        updateOffsets();
    }

    /**
     * Reset state.
     */
    public void resetState() {
        height = 0;
        disabled = true;
        updateVisuals();
    }

    /**
     * Register object.
     */
    public void registerObject(Scene scene, ObjectColorPicker objectPicker) {
        scene.addChild(countryBase);
        scene.addChild(countryTop);
        scene.addChild(countryName);

        objectPicker.registerObject(countryBase);
        objectPicker.registerObject(countryTop);
        objectPicker.registerObject(countryName);
    }

    /**
     * Check whether object3D belongs to country.
     *
     * @param object Object3D
     * @return True if it belongs
     */
    public boolean containsObject(final Object3D object) {
        return countryBase == object || countryTop == object || countryName == object;
    }

    /**
     * Set the height of the country.
     *
     * @param height New height
     */
    public void setHeight(int height) {
        this.height = height;
        if (this.height > Gameplay.Settings.MAX_COUNTRY_HEIGHT) {
            this.height = 0;
        }
        updateVisuals();
    }

    /**
     * When country tapped.
     */
    public void onPicked() {
        if (disabled) {
            return;
        }
        setHeight(++height);
        updateVisuals();
    }

    private void updateOffsets() {
        countryBase.setX(worldOffset.x);
        countryBase.setZ(worldOffset.z);
        countryTop.setX(worldOffset.x);
        countryTop.setZ(worldOffset.z);
        countryName.setX(countryMiddle.getX() + worldOffset.x);
        countryName.setZ(-countryMiddle.getY() + worldOffset.z);
    }

    /**
     * Update visuals.
     */
    private void updateVisuals() {

        countryTop.setVisible(visible);
        countryBase.setVisible(visible);
        countryName.setVisible(visible);

        if (!visible) {
            return;
        }

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

            float colorRatio = (height - 1) / (float) (Gameplay.Settings.MAX_COUNTRY_HEIGHT - 1);
            countryBaseMaterial.setColor(getBaseColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio)));
            countryTopMaterial.setColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio));
        }

        if (disabled) {
            countryBaseMaterial.setColor(getBaseColor(COLOR_DISABLED));
            countryTopMaterial.setColor(COLOR_DISABLED);
        }

    }

    /**
     * Get the base color.
     *
     * @param topColor Top color
     * @return Base color
     */
    private int getBaseColor(int topColor) {
        return ColorUtils.blendARGB(COLOR_BLACK, topColor, 0.5f);
    }

    /**
     * Get current height of the country.
     *
     * @return Height of the country
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get country.
     *
     * @return Country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Disable/enable the country.
     *
     * @param disabled New disabled state
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        updateVisuals();
    }

    /**
     * Set colors for countries.
     *
     * @param minColor Minimum color
     * @param maxColor Maximum color
     */
    public void setColors(int minColor, int maxColor) {
        this.minColor = minColor;
        this.maxColor = maxColor;
        updateVisuals();
    }

    /**
     * Set visibility of the country.
     *
     * @param visible New visible value
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        updateVisuals();
    }

    public void setWorldOffset(Vector3 worldOffset) {
        this.worldOffset = worldOffset;
        updateOffsets();
    }
}
