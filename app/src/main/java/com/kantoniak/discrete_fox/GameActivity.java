package com.kantoniak.discrete_fox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kantoniak.discrete_fox.ar.EasyARController;
import com.kantoniak.discrete_fox.ar.EasyARRenderingDelegate;
import com.kantoniak.discrete_fox.ar.UpdateBackgroundAndMatricesCallback;
import com.kantoniak.discrete_fox.ar.ViewMatrixOverrideCamera;
import com.kantoniak.discrete_fox.ask.Answer;
import com.kantoniak.discrete_fox.ask.AnswersAdapter;
import com.kantoniak.discrete_fox.ask.Question;
import com.kantoniak.discrete_fox.ask.QuestionChest;
import com.kantoniak.discrete_fox.game_mechanics.Gameplay;
import com.kantoniak.discrete_fox.scene.ARRenderingDelegate;
import com.kantoniak.discrete_fox.scene.CountryInstance;
import com.kantoniak.discrete_fox.scene.GameSurfaceView;
import com.kantoniak.discrete_fox.scene.Map;
import com.kantoniak.discrete_fox.scene.MapRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    public static final String MESSAGE_SCORE = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_SCORE";
    public static final String MESSAGE_SCORE_OUT_OF = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_SCORE_OUT_OF";
    public static final String MESSAGE_IS_HIGHSCORE = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_IS_HIGHSCORE";

    // Screens
    @BindView(R.id.overlay) ViewGroup mOverlayView;
    @BindView(R.id.screen_question) ViewGroup mScreenQuestion;

    // screen_question
    @BindView(R.id.button_close) View mCloseView;
    @BindView(R.id.question) TextView mQuestionTextView;
    @BindView(R.id.round_progress) TextView mRoundProgress;
    @BindView(R.id.button_zoom_in) View mZoomIn;
    @BindView(R.id.button_zoom_out) View mZoomOut;

    @BindView(R.id.legend_high_text) TextView mHighTextView;
    @BindView(R.id.legend_mid_text) TextView mMidTextView;
    @BindView(R.id.legend_low_text) TextView mLowTextView;
    @BindView(R.id.legend_high_color) View mHighColorView;
    @BindView(R.id.legend_mid_color) View mMidColorView;
    @BindView(R.id.legend_low_color) View mLowColorView;
    @BindView(R.id.next_button_icon) ImageView mNextIcon;

    @BindView(R.id.answers_recycler) RecyclerView mAnswersRecycler;
    @BindView(R.id.answers_container) LinearLayout mAnswersContainer;

    private Gameplay gameplay;
    boolean showingAnswers = false;
    private AnswersAdapter answersAdapter;

    // Gameplay part
    private static final String TAG = GameActivity.class.getSimpleName();
    private static final String SCORE_PREFS = "score";
    private static final int NUMBEROFCOUNTRIES = 5;

    @BindView(R.id.game_map_preview) GameSurfaceView gameMapPreview;

    private final EasyARController arController = new EasyARController();
    private final ViewMatrixOverrideCamera camera = new ViewMatrixOverrideCamera();
    private final Map map = new Map();
    private MapRenderer renderer;

    private ArrayList<Question> generateQuestions() {
        QuestionChest qc = new QuestionChest(getResources(), getApplicationContext(), NUMBEROFCOUNTRIES);
        ArrayList<Question> questions = new ArrayList<>();
        for (int i = 0; i < qc.numberOfQuestions(); i++) {
            questions.add(qc.getQuestion(i));
        }
        return questions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        setupAnswersRecycler();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setupGameSurfaceView();
        startGame();
    }

    public void setupAnswersRecycler() {
        mAnswersRecycler.setHasFixedSize(true);
        mAnswersRecycler.setLayoutManager(new LinearLayoutManager(this));

        answersAdapter = new AnswersAdapter();
        mAnswersRecycler.setAdapter(answersAdapter);
    }

    private void setupGameSurfaceView() {

        renderer = new MapRenderer(this, map, camera);
        gameMapPreview.setSurfaceRenderer(renderer);

        ARRenderingDelegate arDelegate = new EasyARRenderingDelegate(arController, camera);
        renderer.setArRenderingDelegate(arDelegate);
        gameMapPreview.setArRenderingDelegate(arDelegate);

        UpdateBackgroundAndMatricesCallback updateMatricesCallback = new UpdateBackgroundAndMatricesCallback(arController, camera);
        renderer.getCurrentScene().registerFrameCallback(updateMatricesCallback);
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

    private boolean updateHighScore(int newScore) {
        SharedPreferences prefs = getSharedPreferences("highscore", Context.MODE_PRIVATE);
        int res = prefs.getInt(SCORE_PREFS, 0);
        if (res >= newScore) {
            return false;
        }
        prefs.edit().putInt(SCORE_PREFS, newScore).apply();
        return true;
    }

    private void presentFinalScreen(Gameplay gameplay) {
        boolean hs = updateHighScore(gameplay.getResult());

        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra(MESSAGE_SCORE, gameplay.getResult());
        intent.putExtra(MESSAGE_SCORE_OUT_OF, gameplay.getMaxResult());
        intent.putExtra(MESSAGE_IS_HIGHSCORE, hs);
        startActivity(intent);
        finish();
    }

    public void startGame() {
        showingAnswers = false;
        mAnswersContainer.setVisibility(View.INVISIBLE);
        map.reset();
        //TODO Call generateQuestions on application start.
        gameplay = new Gameplay(generateQuestions(), NUMBEROFCOUNTRIES);
        Question question = gameplay.getCurrentQuestion();
        showQuestion(question);

        showingAnswers = false;
        showScreen(R.id.screen_question);
    }

    @OnClick(R.id.button_close)
    public void buttonClose() {
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }

    @OnClick(R.id.button_zoom_in)
    public void zoomIn() {
        camera.zoomIn();
    }

    @OnClick(R.id.button_zoom_out)
    public void zoomOut() {
        camera.zoomOut();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick(View view) {
        if (showingAnswers) {
            mNextIcon.setImageResource(R.drawable.ic_done_24dp);
            showingAnswers = false;
            mAnswersContainer.setVisibility(View.INVISIBLE);

            Question nextQuestion = gameplay.finishQuestion(getApplicationContext(), map);
            if (nextQuestion == null) {
                showingAnswers = false;
                presentFinalScreen(gameplay);
                return;
            }
            showQuestion(nextQuestion);
        } else {
            Question currentQuestion = gameplay.getCurrentQuestion();
            List<String> countries = currentQuestion.getCountries();
            List<Answer> answers = currentQuestion.getAnswers();
            answers.sort(Comparator.comparing(Answer::getValueRaw).reversed());
            answersAdapter.updateAnswers(answers);

            mAnswersContainer.setVisibility(View.VISIBLE);
            mNextIcon.setImageResource(R.drawable.ic_trending_flat_24dp);
            showingAnswers = true;
            for (String code : countries) {
                CountryInstance countryInstance = map.getCountry(code);
                int targetHeight = currentQuestion.getCorrectAnswer(CountryUtil.eurostatToName(code));
                countryInstance.setHeight(targetHeight);
            }
        }
    }

    private void showQuestion(Question question) {
        map.reset();
        question.getCountries().forEach(map::enableCountry);

        mQuestionTextView.setText(question.getDesc());
        mRoundProgress.setText("" + (gameplay.getCurrentQuestionInt() + 1) + "/" + gameplay.NUMBEROFQUESTIONS);

        mHighTextView.setText(question.getMaxLabel());
        mMidTextView.setText(question.getMidLabel());
        mLowTextView.setText(question.getMinLabel());

        int maxColor = question.getCategory().getMaxColor();
        int minColor = question.getCategory().getMinColor();
        int midColor = ColorUtils.blendARGB(minColor, maxColor, 0.5f);

        map.setColors(minColor, maxColor);

        mHighColorView.setBackgroundColor(maxColor);
        mMidColorView.setBackgroundColor(midColor);
        mLowColorView.setBackgroundColor(minColor);
        // TODO mp3 question
        try {
            int objFileId = getApplicationContext().getResources().getIdentifier("q" + String.valueOf(gameplay.getCurrentQuestionInt() + 1), "raw", getApplicationContext().getPackageName());
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), objFileId);
            mp.start();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameMapPreview != null) {
            gameMapPreview.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (gameMapPreview != null) {
            gameMapPreview.onPause();
        }
        super.onPause();
    }

    @OnTouch(R.id.game_map_preview)
    public boolean onTouch(View view, MotionEvent event) {
        if (!showingAnswers) {
            renderer.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }
}
