package com.kantoniak.discrete_fox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.ColorUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.easyar.Engine;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION = 0;

    private ARSurfaceView surfaceView;
    private final Map map = new Map();

    // Screens
    @BindView(R.id.overlay) ViewGroup mOverlayView;
    @BindView(R.id.screen_menu) ViewGroup mScreenMenu;
    @BindView(R.id.screen_question) ViewGroup mScreenQuestion;

    // screen_menu
    @BindView(R.id.start_button) Button mStartButton;

    // screen_question
    @BindView(R.id.question) TextView mQuestionTextView;
    @BindView(R.id.legend_high_text) TextView mHighTextView;
    @BindView(R.id.legend_mid_text) TextView mMidTextView;
    @BindView(R.id.legend_low_text) TextView mLowTextView;
    @BindView(R.id.legend_high_color) View mHighColorView;
    @BindView(R.id.legend_mid_color) View mMidColorView;
    @BindView(R.id.legend_low_color) View mLowColorView;

    // screen_score
    @BindView(R.id.score_points) TextView mScoreTextView;
    @BindView(R.id.score_play_again) TextView mPlayAgainButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private ArrayList<Question> generateQuestions() {
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
        return questions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupAR();
        showScreen(R.id.screen_menu);
        requestCameraPermission();
    }

    public void setupAR() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!Engine.initialize(this, getString(R.string.easy_ar_key))) {
            Log.e("AR Box", "Initialization Failed.");
        }

        surfaceView = new ARSurfaceView(this, map);
    }

    private void hideAllScreens() {
        for (int i = 0; i < mOverlayView.getChildCount(); i++) {
            mOverlayView.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private void showScreen(int screenId) {
        hideAllScreens();
        mOverlayView.findViewById(screenId).setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.start_button)
    public void startGame(View view) {

        map.disableAllCountries();
        //TODO Call generateQuestions on application start.
        final Gameplay gameplay = new Gameplay(generateQuestions(), 3);
        Question question = gameplay.getCurrentQuestion();
        showQuestion(question);
        LinearLayout next = findViewById(R.id.nextButton);
        next.setOnClickListener(btn -> {
            Question nextQuestion = gameplay.finishQuestion(getApplicationContext(), map);
            if (nextQuestion == null) {
                showScreen(R.id.screen_score);
                return;
            }
            showQuestion(nextQuestion);
        });

        showScreen(R.id.screen_question);
    }

    @OnClick(R.id.score_play_again)
    public void restartGame(View view) {
        startGame(view);
    }

    private void showQuestion(Question question) {
        map.disableAllCountries();
        for (String code: question.getCountries()) {
            map.enableCountry(code);
        }
        mQuestionTextView.setText(question.getDesc());

        mHighTextView.setText(question.getHighLabel());
        mMidTextView.setText(question.getMidLabel());
        mLowTextView.setText(question.getLowLabel());

        //TODO(kedzior): Colors in question, alpha goes in first two chars (FF)
        int maxColor = question.getMmaxColor();
        int minColor = question.getMminColor();
        int midColor = ColorUtils.blendARGB(minColor, maxColor, 0.5f);

        mHighColorView.setBackgroundColor(maxColor);
        mMidColorView.setBackgroundColor(midColor);
        mLowColorView.setBackgroundColor(minColor);
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
