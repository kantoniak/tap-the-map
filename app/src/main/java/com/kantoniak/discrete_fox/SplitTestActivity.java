package com.kantoniak.discrete_fox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kantoniak.discrete_fox.new_ar.ARUtils;
import com.kantoniak.discrete_fox.scene.MapRenderer;

import org.rajawali3d.view.SurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplitTestActivity extends AppCompatActivity {

    private static final String TAG = SplitTestActivity.class.getSimpleName();
    private static final int CAMERA_PERMISSION = 0;

    @BindView(R.id.camera_preview) GLSurfaceView cameraFrameView;
    @BindView(R.id.game_map_preview) SurfaceView gameMapPreview;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_test);
        ButterKnife.bind(this);

        ARUtils.initializeEngine(this);
        // FIXME: This should be called earlier, from some other Activity.
        // FIXME: Camera will not show up the first time because device is opened earlier.
        requestCameraPermission();

        gameMapPreview.setSurfaceRenderer(new MapRenderer(this));
    }

    private void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            onCameraRequestSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION) {
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    onCameraRequestFailure();
                }
            }
            if (!executed) {
                onCameraRequestSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void onCameraRequestSuccess() {
        // Do nothing
    }

    private void onCameraRequestFailure() {
        // FIXME: Show error on camera request failure
        Log.e(TAG, "Camera request failed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraFrameView != null) {
            cameraFrameView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (cameraFrameView != null) {
            cameraFrameView.onPause();
        }
        super.onPause();
    }
}
