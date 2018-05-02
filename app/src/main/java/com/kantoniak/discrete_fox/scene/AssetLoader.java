package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.kantoniak.discrete_fox.BuildConfig;
import com.kantoniak.discrete_fox.Country;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
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
        int fileId = context.getResources().getIdentifier(filename, "raw", BuildConfig.APPLICATION_ID);
        LoaderOBJ loader = new LoaderOBJ(context.getResources(), textureManager, fileId);

        try {
            loader.parse();
        } catch (ParsingException e) {
            throw new RuntimeException("OBJ load failed", e);
        }

        return loader.getParsedObject();
    }

    public Texture loadTexture(String drawableName) {
        int texDrawableId = context.getResources().getIdentifier(drawableName, "drawable", BuildConfig.APPLICATION_ID);
        return new Texture(drawableName, texDrawableId);
    }

    public Object3D loadCountryBase(Country country) {
        return loadObj(getCountryBaseObjName(country));
    }

    public Object3D loadCountryTop(Country country) {
        return loadObj(getCountryTopObjName(country));
    }

    public Texture loadCountryNameTexture(Country country) {
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
