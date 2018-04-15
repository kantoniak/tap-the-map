package com.kantoniak.discrete_fox;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
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
    private static final String SCORE_PREFS = "score";

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
    @BindView(R.id.next_button_icon) ImageView mNextIcon;
    @BindView(R.id.list_view_answers) ListView mListView;
    @BindView(R.id.list_view_linear_layout) LinearLayout mListViewLinearLayout;

    private Gameplay gameplay;
    boolean showingAnswers = false;

    // screen_score
    @BindView(R.id.score_points) TextView mScoreTextView;
    @BindView(R.id.great_job_tv) TextView mGreatJobTv;
    @BindView(R.id.score_play_again) TextView mPlayAgainButton;
    @BindView(R.id.facebook_share_button) ShareButton mShareButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private ArrayList<Question> generateQuestions() {
        QuestionChest qc = new QuestionChest(getResources());
        int n = qc.numberOfQuestions();
        Question[] q = new Question[n];
        for (int i = 0; i < n; i++) {
            q[i] = qc.getQuestion(i);
        }
        ArrayList<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            questions.add(q[i]);
        }
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

    private int updateHighScore(int newScore) {
        SharedPreferences prefs = getSharedPreferences("highscore", Context.MODE_PRIVATE);
        int res = prefs.getInt(SCORE_PREFS, 0);
        if (res < newScore) {
            prefs.edit().putInt(SCORE_PREFS, newScore).apply();
            return 1;
        }
        return 0;
    }

    private void presentFinalScreen(Gameplay gameplay) {
        int hs = updateHighScore(gameplay.getResult());

        showScreen(R.id.screen_score);
        if (hs == 1) {
            mGreatJobTv.setText(getResources().getString(R.string.new_high_score));
        } else if ((gameplay.getResult()*1.0) / gameplay.getMaxResult() < 0.2) {
            mGreatJobTv.setText(getResources().getString(R.string.score_needs_improvement));
        } else {
            mGreatJobTv.setText(getResources().getString(R.string.score_great_job));
        }
        mScoreTextView.setText(String.valueOf(gameplay.getResult()) + "/" + String.valueOf((gameplay.getMaxResult())));

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setQuote("Whatever")
                .setContentUrl(Uri.parse("https://stat.gov.pl/"))
                .build();
        mShareButton.setShareContent(content);
    }

    @OnClick(R.id.start_button)
    public void startGame(View view) {
        map.disableAllCountries();
        //TODO Call generateQuestions on application start.
        gameplay = new Gameplay(generateQuestions(), 5);
        Question question = gameplay.getCurrentQuestion();
        showQuestion(question);

        showingAnswers = false;
        showScreen(R.id.screen_question);
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick(View view) {
        if (showingAnswers) {
            mNextIcon.setImageResource(R.drawable.ic_done_24dp);
            showingAnswers = false;
            mListViewLinearLayout.setVisibility(View.INVISIBLE);

            Question nextQuestion = gameplay.finishQuestion(getApplicationContext(), map);
            if (nextQuestion == null) {
                presentFinalScreen(gameplay);
                return;
            }
            showQuestion(nextQuestion);
        } else {
            Question currentQuestion = gameplay.getCurrentQuestion();
            String[] countries = currentQuestion.getCountries();
            String[] forList = new String[countries.length];
            CountryUtil cu = new CountryUtil();
            for (int i = 0; i < countries.length; i++) {
                int r = currentQuestion.getCorrectAnswer(cu.convert(countries[i]));
                double res = currentQuestion.getAnsDouble().get(cu.convert(countries[i]));
                String ress = String.format("%.2f%%", res);
                if (currentQuestion.getDesc() == getResources().getString(R.string.question_gdp)) {
                    if (i == 1) {
                        ress = String.format("%.2fT€", res/1000000);
                    } else {
                        ress = String.format("%.2fG€", res/1000);
                    }
                } else if (currentQuestion.getDesc() == getResources().getString(R.string.question_population_density)) {
                    ress = String.format("%.2f/km2", res);
                }
                if (countries[i].equals("de")) {
                    forList[i] = "Germany - " + ress;
                } else {
                    forList[i] = cu.convert(countries[i]) + " - " + ress;
                }
            }
            ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, forList);
            mListView.setAdapter(test);
            mListViewLinearLayout.setVisibility(View.VISIBLE);
            mNextIcon.setImageResource(R.drawable.ic_trending_flat_24dp);
            showingAnswers = true;
        }
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

        mHighTextView.setText(question.getMmaxLabel());
        mMidTextView.setText(question.getMmidLabel());
        mLowTextView.setText(question.getMminLabel());

        int maxColor = question.getMmaxColor();
        int minColor = question.getMminColor();
        int midColor = ColorUtils.blendARGB(minColor, maxColor, 0.5f);

        map.setColors(minColor, maxColor);

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
