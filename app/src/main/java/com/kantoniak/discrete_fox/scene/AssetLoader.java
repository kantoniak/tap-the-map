package com.kantoniak.discrete_fox.scene;

import android.content.Context;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;

class AssetLoader {

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
}
