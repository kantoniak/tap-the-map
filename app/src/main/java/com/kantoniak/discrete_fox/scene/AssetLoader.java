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

/**
 * Class used for loading assets.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class AssetLoader {

    private final Context context;
    private final TextureManager textureManager;

    AssetLoader(Context context, TextureManager textureManager) {
        this.context = context;
        this.textureManager = textureManager;
    }

    /**
     * Loads 3D object.
     * @param filename Name of the file with object.
     * @return Loaded 3D object
     */
    private Object3D loadObj(String filename) {
        int fileId = context.getResources().getIdentifier(filename, "raw", BuildConfig.APPLICATION_ID);
        LoaderOBJ loader = new LoaderOBJ(context.getResources(), textureManager, fileId);

        try {
            loader.parse();
        } catch (ParsingException e) {
            throw new RuntimeException("OBJ load failed", e);
        }

        return loader.getParsedObject();
    }

    /**
     * Loads texture.
     * @param drawableName Name of the texture
     * @return Loaded texture
     */
    public Texture loadTexture(String drawableName) {
        int texDrawableId = context.getResources().getIdentifier(drawableName, "drawable", BuildConfig.APPLICATION_ID);
        return new Texture(drawableName, texDrawableId);
    }

    /**
     * Load country base.
     * @param country Country for which the base will be loaded
     * @return Loaded object for country base
     */
    public Object3D loadCountryBase(Country country) {
        return loadObj(getCountryBaseObjName(country));
    }

    /**
     * Load country top.
     * @param country Country for which the top will be loaded
     * @return Loaded object for country top
     */
    public Object3D loadCountryTop(Country country) {
        return loadObj(getCountryTopObjName(country));
    }

    /**
     * Load country name texture.
     * @param country Country for which the name texture will be loaded
     * @return Loaded texture for country name
     */
    public Texture loadCountryNameTexture(Country country) {
        return loadTexture(getCountryNameDrawableName(country));
    }

    /**
     * Get resource string for country base object.
     * @param country Country for which string for the base we need
     * @return Resource string for country base
     */
    public static String getCountryBaseObjName(Country country) {
        return "country_base_" + country.getEuCode() + "_obj";
    }

    /**
     * Get resource string for country top object.
     * @param country Country for which string for the top we need
     * @return Resource string for country top
     */
    public static String getCountryTopObjName(Country country) {
        return "country_top_" + country.getEuCode() + "_obj";
    }

    /**
     * Get resource string for drawable name.
     * @param country Country for which string for the drawable name we need
     * @return Resource string for drawable name
     */
    public static String getCountryNameDrawableName(Country country) {
        return "country_" + country.getEuCode() + "_plate";
    }
}
