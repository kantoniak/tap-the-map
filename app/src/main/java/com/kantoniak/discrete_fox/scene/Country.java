package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import com.kantoniak.discrete_fox.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector2;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
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

    private static float NAME_PLANE_SCALE = 0.5f;
    private static float NAME_PLANE_Z = 0.4f;
    private Object3D namePlane;
    private Vector3 namePlanePos;

    private static int DEFAULT_COLOR = 0xFFE0E0E0;
    private static int DISABLED_COLOR = 0xFF9E9E9E;
    private static int BLACK_COLOR = 0xFF000000;
    private int minColor;
    private int maxColor;

    private static float TOP_HEIGHT = 0.2f;
    private static float NEAR_ZERO_HEIGHT = 0.001f;

    public Country(String code, int maxHeight, int minColor, int maxColor, Vector2 middlePos) {
        this.code = code;
        this.maxHeight = maxHeight;
        this.minColor = minColor;
        this.maxColor = maxColor;
        this.disabled = true;
        this.namePlanePos = new Vector3(-middlePos.getX(), middlePos.getY(), NAME_PLANE_Z);
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

        namePlane = new Plane();
        namePlane.setPosition(namePlanePos);
        namePlane.setDoubleSided(true);
        namePlane.setScaleX(-NAME_PLANE_SCALE);
        namePlane.setScaleZ(NAME_PLANE_SCALE);
        namePlane.rotate(Vector3.Axis.X, -90);
        Material planeMaterial = new Material();
        planeMaterial.setColor(0x333333);
        namePlane.setMaterial(planeMaterial);

        try {
            final String texDrawableName = "country_" + code + "_plate";
            int texDrawableId = context.getResources().getIdentifier(texDrawableName, "drawable", context.getPackageName());
            planeMaterial.addTexture(textureManager.addTexture(new Texture(texDrawableName, texDrawableId)));
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

        zeroChoice();
    }

    private void setupMaterials() {
        baseMaterial = new Material();
        topMaterial = new Material();
    }

    public void registerObject(Scene scene, ObjectColorPicker objectPicker) {
        scene.addChild(baseObject);
        scene.addChild(topObject);
        scene.addChild(namePlane);
        objectPicker.registerObject(baseObject);
        objectPicker.registerObject(topObject);
        objectPicker.registerObject(namePlane);
    }

    public boolean containsObject(final Object3D object) {
        return baseObject == object || topObject == object || namePlane == object;
    }

    public void onPicked() {
        if (disabled) {
            return;
        }
        setHeight(++height);
        //TODO mp3 play height

        //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.);
        //mp.start();
    }

    public void setHeight(int height) {
        if (height > maxHeight) {
            zeroChoice();
            return;
        }

        baseObject.setScaleZ(0.5f * height);
        topObject.setZ(0.5f * baseObject.getScaleZ());
        namePlane.setZ(0.5f * baseObject.getScaleZ() + NAME_PLANE_Z);

        float colorRatio = (height - 1) / (float)(maxHeight - 1);
        baseMaterial.setColor(getBaseColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio)));
        topMaterial.setColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio));
    }

    private void zeroChoice() {
        height = 0;
        baseObject.setScaleZ(NEAR_ZERO_HEIGHT);
        topObject.setZ(NEAR_ZERO_HEIGHT);
        namePlane.setZ(NAME_PLANE_Z);
        if (disabled) {
            baseMaterial.setColor(DISABLED_COLOR);
            topMaterial.setColor(DISABLED_COLOR);
            namePlane.setVisible(false);
        } else {
            baseMaterial.setColor(DEFAULT_COLOR);
            topMaterial.setColor(DEFAULT_COLOR);
            namePlane.setVisible(true);
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
            namePlane.setVisible(true);
        }
    }

    public void setColors(int minColor, int maxColor) {
        this.minColor = minColor;
        this.maxColor = maxColor;
    }

}
