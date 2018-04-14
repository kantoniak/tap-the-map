package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import com.kantoniak.discrete_fox.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.scene.Scene;
import org.rajawali3d.util.ObjectColorPicker;

public class Country {

    private final String code;
    private final int maxHeight;
    private int height = 0;

    private Object3D object;
    private Material material;

    private static int DEFAULT_COLOR = 0xF5F5F5;
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

        object = loader.getParsedObject();
        object.rotate(Vector3.Axis.X, -90);
        object.setScaleX(-1.f);
        object.setDoubleSided(true);

        setupMaterial();
        object.setMaterial(material);
        material.setColor(DEFAULT_COLOR);

        object.setScaleZ(0);
    }

    private void setupMaterial() {
        material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
    }

    public void registerObject(Scene scene, ObjectColorPicker objectPicker) {
        scene.addChild(object);
        objectPicker.registerObject(object);
        material.setColor(DEFAULT_COLOR);
    }

    public Object3D getObject() {
        return object;
    }

    public void onPicked() {
        nextState();
    }

    public void nextState() {
        height++;

        if (height > maxHeight) {
            height = 0;
            object.setScaleZ(0.01f);
            material.setColor(DEFAULT_COLOR);
            return;
        }

        object.setScaleZ(0.5f * height);
        float colorRatio = (height - 1) / (float)(maxHeight - 1);
        material.setColor(ColorUtils.blendARGB(minColor, maxColor, colorRatio));
    }
}
