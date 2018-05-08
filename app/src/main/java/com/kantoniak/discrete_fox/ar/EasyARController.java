package com.kantoniak.discrete_fox.ar;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;

import cn.easyar.CameraCalibration;
import cn.easyar.CameraDevice;
import cn.easyar.CameraDeviceFocusMode;
import cn.easyar.CameraDeviceType;
import cn.easyar.CameraFrameStreamer;
import cn.easyar.Frame;
import cn.easyar.ImageTarget;
import cn.easyar.ImageTracker;
import cn.easyar.Matrix44F;
import cn.easyar.Renderer;
import cn.easyar.StorageType;
import cn.easyar.Target;
import cn.easyar.TargetInstance;
import cn.easyar.TargetStatus;
import cn.easyar.Vec2I;
import cn.easyar.Vec4I;

import static com.kantoniak.discrete_fox.ar.EasyARUtils.TAG_AR;

/**
 * Class responsible for east AR controller.
 */
public class EasyARController {

    private final static String TARGETS_FILE = "targets.json";

    private CameraDevice camera;
    private CameraFrameStreamer streamer;
    private ArrayList<ImageTracker> trackers;

    private Renderer videobg_renderer;
    private boolean viewport_changed = false;
    private Vec2I view_size = new Vec2I(0, 0);
    private int rotation = 0;
    private Vec4I viewport = new Vec4I(0, 0, 1280, 720);

    private Matrix44F viewMatrix;
    private Matrix44F projectionMatrix;

    private OnScanListener mListener;

    public EasyARController() {
        trackers = new ArrayList<>();
    }

    /**
     * Load all from json file.
     *
     * @param tracker Image tracker
     */
    @SuppressLint("DefaultLocale")
    private void loadAllFromJsonFile(ImageTracker tracker) {
        for (ImageTarget target : ImageTarget.setupAll(TARGETS_FILE, StorageType.Assets)) {
            tracker.loadTarget(target, (loadedTarget, status) -> {
                if (status) {
                    String message = String.format("Loaded target %s (#%d)", loadedTarget.name(), loadedTarget.runtimeID());
                    Log.i(TAG_AR, message);
                } else {
                    String message = String.format("Failed loading target %s (#%d)", loadedTarget.name(), loadedTarget.runtimeID());
                    Log.e(TAG_AR, message);
                }
            });
        }
    }

    /**
     * Initialize.
     *
     * @return True if succeeded
     */
    public boolean initialize() {
        camera = new CameraDevice();
        streamer = new CameraFrameStreamer();
        streamer.attachCamera(camera);

        boolean status = camera.open(CameraDeviceType.Default);
        camera.setSize(new Vec2I(1280, 720));

        if (!status) {
            return false;
        }
        ImageTracker tracker = new ImageTracker();
        tracker.attachStreamer(streamer);
        loadAllFromJsonFile(tracker);
        trackers.add(tracker);

        return true;
    }

    /**
     * Dispose.
     */
    public void dispose() {
        for (ImageTracker tracker : trackers) {
            tracker.dispose();
        }
        trackers.clear();
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
            videobg_renderer = null;
        }
        if (streamer != null) {
            streamer.dispose();
            streamer = null;
        }
        if (camera != null) {
            camera.dispose();
            camera = null;
        }
    }

    /**
     * Start.
     *
     * @return True if succeeded
     */
    public boolean start() {
        boolean status = (camera != null) && camera.start();
        status &= (streamer != null) && streamer.start();

        if (!status) {
            return false;
        }

        camera.setFocusMode(CameraDeviceFocusMode.Continousauto);
        for (ImageTracker tracker : trackers) {
            status &= tracker.start();
        }
        return status;
    }

    /**
     * Stop.
     */
    public void stop() {
        for (ImageTracker tracker : trackers) {
            tracker.stop();
        }
        if (streamer != null) {
            streamer.stop();
        }
        if (camera != null) {
            camera.stop();
        }
    }

    /**
     * Initialize GL.
     */
    public void initGL() {
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
        }
        videobg_renderer = new Renderer();
    }

    /**
     * Resize GL.
     *
     * @param width  Width
     * @param height Height
     */
    public void resizeGL(int width, int height) {
        view_size = new Vec2I(width, height);
        viewport_changed = true;
    }

    /**
     * Update viewport.
     */
    private void updateViewport() {
        CameraCalibration calib = camera != null ? camera.cameraCalibration() : null;
        int rotation = calib != null ? calib.rotation() : 0;
        if (rotation != this.rotation) {
            this.rotation = rotation;
            viewport_changed = true;
        }
        if (viewport_changed) {
            Vec2I size = new Vec2I(1, 1);
            if ((camera != null) && camera.isOpened()) {
                size = camera.size();
            }
            if (rotation == 90 || rotation == 270) {
                size = new Vec2I(size.data[1], size.data[0]);
            }
            float scaleRatio = Math.max((float) view_size.data[0] / (float) size.data[0], (float) view_size.data[1] / (float) size.data[1]);
            Vec2I viewport_size = new Vec2I(Math.round(size.data[0] * scaleRatio), Math.round(size.data[1] * scaleRatio));
            viewport = new Vec4I((view_size.data[0] - viewport_size.data[0]) / 2, (view_size.data[1] - viewport_size.data[1]) / 2, viewport_size.data[0], viewport_size.data[1]);

            if ((camera != null) && camera.isOpened())
                viewport_changed = false;
        }
    }

    /**
     * Render.
     */
    public void render() {

        if (videobg_renderer != null) {
            Vec4I default_viewport = new Vec4I(0, 0, view_size.data[0], view_size.data[1]);
            GLES20.glViewport(default_viewport.data[0], default_viewport.data[1], default_viewport.data[2], default_viewport.data[3]);
            if (videobg_renderer.renderErrorMessage(default_viewport)) {
                return;
            }
        }

        if (streamer == null) {
            return;
        }
        Frame frame = streamer.peek();
        try {
            updateViewport();
            GLES20.glViewport(viewport.data[0], viewport.data[1], viewport.data[2], viewport.data[3]);

            if (videobg_renderer != null) {
                videobg_renderer.render(frame, viewport);
            }

            for (TargetInstance targetInstance : frame.targetInstances()) {
                int status = targetInstance.status();
                if (status == TargetStatus.Tracked) {
                    Target target = targetInstance.target();
                    ImageTarget imagetarget = target instanceof ImageTarget ? (ImageTarget) (target) : null;
                    if (imagetarget == null) {
                        continue;
                    }
                    viewMatrix = targetInstance.poseGL();
                    projectionMatrix = camera.projectionGL(0.2f, 500.f);

                    if (mListener != null) {
                        mListener.onTracked(target);
                    }
                }
            }
        } finally {
            frame.dispose();
        }
    }

    /**
     * Get projection matrix.
     *
     * @return Projection matrix
     */
    public Matrix44F getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Get view matrix.
     *
     * @return View matrix
     */
    public Matrix44F getViewMatrix() {
        return viewMatrix;
    }

    /**
     * On scan listener interface.
     */
    public interface OnScanListener {
        /**
         * When tracked.
         */
        void onTracked(Target target);
    }

    /**
     * Set on scan listener.
     */
    public void setOnScanListener(OnScanListener listener) {
        this.mListener = listener;
    }
}
