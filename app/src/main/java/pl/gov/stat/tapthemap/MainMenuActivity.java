package pl.gov.stat.tapthemap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import pl.gov.stat.tapthemap.ar.EasyARUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity for displaying the main menu.
 */
public class MainMenuActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION = 0;

    @BindView(R.id.main_bg)
    ImageView mMainBackground;
    @BindView(R.id.screen_permission)
    View mScreenPermission;

    /**
     * Set up the activity.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
        EasyARUtils.initializeEngine(this);

        // Start background animation
        AnimatedVectorDrawable bgDrawable = (AnimatedVectorDrawable) mMainBackground.getDrawable();
        bgDrawable.start();

        mScreenPermission.setOnTouchListener((view, event) -> true);
    }

    /**
     * New game button clicked.
     */
    @OnClick(R.id.start_button)
    public void onClickStart(View view) {
        requestCameraPermission();
    }

    /**
     * About button clicked.
     */
    @OnClick(R.id.about_button)
    public void onClickAbout(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    /**
     * Camera request granted.
     */
    private void onCameraRequestSuccess() {
        startActivity(new Intent(this, GameActivity.class));
        mScreenPermission.setVisibility(View.GONE);
    }

    /**
     * Camera request rejected.
     */
    private void onCameraRequestFailure() {
        mScreenPermission.setVisibility(View.VISIBLE);
    }

    /**
     * Repeat camera request.
     */
    @OnClick(R.id.button_try_camera_again)
    public void onRetryCamera(View view) {
        requestCameraPermission();
    }

    /**
     * Request camera permission.
     */
    private void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            onCameraRequestSuccess();
        }
    }

    /**
     * Request camera permissions result.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION) {
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    onCameraRequestFailure();
                }
            }
            if (!executed) {
                onCameraRequestSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * When pressed system back button.
     */
    @Override
    public void onBackPressed() {
        if (mScreenPermission.getVisibility() == View.VISIBLE) {
            mScreenPermission.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
