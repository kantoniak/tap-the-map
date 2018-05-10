package pl.gov.stat.tapthemap.scene;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import pl.gov.stat.tapthemap.Country;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.math.vector.Vector2;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Class responsible for rendering the map.
 */
public class MapRenderer extends Renderer implements OnObjectPickedListener {

    private static final Vector2 MAP_SIZE = new Vector2(2.438f, 2.889f);
    private static final Vector3 MAP_CORRECTION = new Vector3(-0.005f, 0, 0.005f);
    private static final Vector3 MAP_MIDDLE = new Vector3(-MapRenderer.MAP_SIZE.getX(), 0, MapRenderer.MAP_SIZE.getY()).multiply(0.5f);

    private final Map map;
    private final AssetLoader loader;
    private RenderingDelegate renderingDelegate;

    private ObjectColorPicker objectPicker;
    private Plane mapBase;

    private boolean visible = true;

    private Vector3 worldOffset = getDefaultWorldOffset();

    public MapRenderer(Context context, Map map) {
        super(context);
        this.map = map;
        this.loader = new AssetLoader(context, mTextureManager);
    }

    /**
     * Set rendering delegate.
     *
     * @param renderingDelegate Rendering delegate
     */
    public void setRenderingDelegate(RenderingDelegate renderingDelegate) {
        this.renderingDelegate = renderingDelegate;
    }

    /**
     * When render surface created.
     */
    @Override
    public void onRenderSurfaceCreated(EGLConfig config, GL10 gl, int width, int height) {
        super.onRenderSurfaceCreated(config, gl, width, height);
        if (renderingDelegate != null) {
            renderingDelegate.onSurfaceCreated();
        }
    }

    /**
     * When render surface size changed.
     */
    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        if (renderingDelegate != null) {
            renderingDelegate.onSurfaceChanged(width, height);
        }
    }

    /**
     * Initialize scene.
     */
    @Override
    protected void initScene() {
        setupObjectPicker();

        mapBase = new Plane();
        mapBase.setDoubleSided(true);
        mapBase.setScale(MAP_SIZE.getX(), 1, MAP_SIZE.getY());
        mapBase.setPosition(MAP_CORRECTION);
        mapBase.rotate(Vector3.Axis.X, +90);
        mapBase.setTransparent(true);
        getCurrentScene().addChild(mapBase);

        try {
            Material mapBaseMaterial = new Material();
            mapBaseMaterial.setColor(0x00000000);
            mapBaseMaterial.addTexture(mTextureManager.addTexture(loader.loadTexture("map_background")));
            mapBase.setMaterial(mapBaseMaterial);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

        showMap(false);
    }

    /**
     * Add country instance.
     *
     * @param countryInstance Which country instance
     */
    public void addCountryInstance(CountryInstance countryInstance) {

        countryInstance.initPositions(MAP_MIDDLE, map.getCountryMiddle(countryInstance.getCountry()));
        countryInstance.registerObject(getCurrentScene(), objectPicker);
        countryInstance.resetState();
        map.addCountryInstance(countryInstance);
    }

    /**
     * Update visibility.
     */
    private void updateVisibility() {
        if (mapBase != null) {
            mapBase.setVisible(visible);
        }
        map.setVisible(visible);
    }

    /**
     * Set up object picker.
     */
    private void setupObjectPicker() {
        objectPicker = new ObjectColorPicker(this);
        objectPicker.setOnObjectPickedListener(this);
    }

    /**
     * When offsets changed.
     */
    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    /**
     * When touch event.
     */
    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            objectPicker.getObjectAt(event.getX(), event.getY());
        }
    }

    /**
     * When object picked.
     *
     * @param object Which object
     */
    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        for (java.util.Map.Entry<Country, CountryInstance> entry : map.getCountries().entrySet()) {
            if (entry.getValue().containsObject(object)) {
                entry.getValue().onPicked();
            }
        }
    }

    /**
     * When no object picked.
     */
    @Override
    public void onNoObjectPicked() {
        // Do nothing
    }

    /**
     * Set camera.
     *
     * @param camera Which camera
     */
    public void setCamera(Camera camera) {
        getCurrentScene().addAndSwitchCamera(camera);
    }

    /**
     * Get loader.
     *
     * @return Asset loader
     */
    public AssetLoader getLoader() {
        return loader;
    }

    /**
     * Get map.
     *
     * @return Map
     */
    public Map getMap() {
        return map;
    }

    /**
     * Show map.
     *
     * @param show Whether to show or not
     */
    public void showMap(boolean show) {
        this.visible = show;
        updateVisibility();
    }

    public void setWorldOffset(Vector3 worldOffset) {
        mapBase.setPosition(worldOffset.clone().add(MAP_MIDDLE.clone().multiply(-1.f)).add(MAP_CORRECTION));
        map.getCountries().forEach((country, instance) -> instance.setWorldOffset(worldOffset));
        this.worldOffset = worldOffset;
    }

    public Vector3 getWorldOffset() {
        return worldOffset;
    }

    public Vector3 getDefaultWorldOffset() {
        return MAP_MIDDLE.clone();
    }
}
