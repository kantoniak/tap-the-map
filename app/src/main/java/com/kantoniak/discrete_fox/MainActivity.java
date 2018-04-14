package com.kantoniak.discrete_fox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kantoniak.discrete_fox.ar.ARSurfaceView;
import com.kantoniak.discrete_fox.communication.Question;
import com.kantoniak.discrete_fox.communication.QuestionChest;
import com.kantoniak.discrete_fox.game_mechanics.Gameplay;
import com.kantoniak.discrete_fox.scene.Map;

import java.util.ArrayList;

import cn.easyar.Engine;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION = 0;

    private ARSurfaceView surfaceView;
    private final Map map = new Map();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuestionChest qc = new QuestionChest(getResources());
        int n = qc.numberOfQuestions();
        Question[] q = new Question[n];
        for (int i = 0; i < n; i++)
        {
            q[i] = qc.getQuestion(i);
        }
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(q[0]);
        questions.add(q[1]);
        questions.add(q[2]);
        final Gameplay gameplay = new Gameplay(questions, 3);
        LinearLayout next = findViewById(R.id.nextButton);
        next.setOnClickListener(view -> {

            Question nextQuestion = gameplay.finishQuestion();
            if (nextQuestion == null) {
                    return;
                }
                Toast.makeText(getApplicationContext(), "Zakonczyles odpowiadanie", Toast.LENGTH_LONG).show();
        TextView title = findViewById(R.id.question);
        title.setText(nextQuestion.getDesc());
/*            } else {
            gameplay.nextQuestion();
            Toast.makeText(getApplicationContext(), "Nowe pytanie", Toast.LENGTH_LONG).show();
        }*/
        });
        setupAR();
        requestCameraPermission();
    }

    public void setupAR() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!Engine.initialize(this, getString(R.string.easy_ar_key))) {
            Log.e("AR Box", "Initialization Failed.");
        }

        surfaceView = new ARSurfaceView(this, map);
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
