package com.kantoniak.discrete_fox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;

import com.kantoniak.discrete_fox.ar.ARSurfaceView;
import com.kantoniak.discrete_fox.communication.Question;
import com.kantoniak.discrete_fox.communication.QuestionChest;

import cn.easyar.Engine;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION = 0;

    private ARSurfaceView surfaceView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuestionChest qc = new QuestionChest();
        // Arrays are soooo cool!
        int n = qc.numberOfQuestions();
        Question[] q = new Question[n];
        for (int i = 0; i < n; i++)
        {
            q[i] = qc.getQuestion(i);
        }
        //q.
        //qc.hasNext();
        //qc.nextQuestion();
        setupAR();
        requestCameraPermission();
    }

    public void setupAR() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!Engine.initialize(this, getString(R.string.easy_ar_key))) {
            Log.e("AR Box", "Initialization Failed.");
        }

        surfaceView = new ARSurfaceView(this);
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
        ((ViewGroup) findViewById(R.id.preview)).addView(surfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void onCameraRequestFailure() {
        Log.e("ARBOX", "CameraRequestFailure");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (surfaceView != null) {
            surfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (surfaceView != null) {
            surfaceView.onPause();
        }
        super.onPause();
    }
}
