package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.opengl.Visibility;
import android.support.annotation.VisibleForTesting;

import com.kantoniak.discrete_fox.country.Country;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class AssetLoader {

    private final Context context;
    private final TextureManager textureManager;

    public AssetLoader(Context context, TextureManager textureManager) {
        this.context = context;
        this.textureManager = textureManager;
    }

    public Object3D loadObj(String filename) {
        int fileId = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
        LoaderOBJ loader = new LoaderOBJ(context.getResources(), textureManager, fileId);

        try {
            loader.parse();
        } catch (ParsingException e) {
            throw new RuntimeException("OBJ load failed", e);
        }

        return loader.getParsedObject();
    }

    public ATexture loadTexture(String drawableName) {
        int texDrawableId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        return textureManager.addTexture(new Texture(drawableName, texDrawableId));
    }

    public Object3D loadCountryBase(Country country) {
        return loadObj(getCountryBaseObjName(country));
    }

    public Object3D loadCountryTop(Country country) {
        return loadObj(getCountryTopObjName(country));
    }

    public ATexture loadCountryNameTexture(Country country) {
        return loadTexture(getCountryNameDrawableName(country));
    }

    @VisibleForTesting
    public static String getCountryBaseObjName(Country country) {
        return "country_base_" + country.getEuCode() + "_obj";
    }

    @VisibleForTesting
    public static String getCountryTopObjName(Country country) {
        return "country_top_" + country.getEuCode() + "_obj";
    }

    @VisibleForTesting
    public static String getCountryNameDrawableName(Country country) {
        return "country_" + country.getEuCode() + "_plate";
    }
}
