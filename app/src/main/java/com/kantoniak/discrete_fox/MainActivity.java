package com.kantoniak.discrete_fox;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.kantoniak.discrete_fox.ar.EasyARController;
import com.kantoniak.discrete_fox.ar.EasyARUtils;
import com.kantoniak.discrete_fox.ar.EasyARRenderingDelegate;
import com.kantoniak.discrete_fox.ar.UpdateBackgroundAndMatricesCallback;
import com.kantoniak.discrete_fox.ar.ViewMatrixOverrideCamera;
import com.kantoniak.discrete_fox.communication.Question;
import com.kantoniak.discrete_fox.communication.QuestionChest;
import com.kantoniak.discrete_fox.game_mechanics.Gameplay;
import com.kantoniak.discrete_fox.scene.ARRenderingDelegate;
import com.kantoniak.discrete_fox.scene.GameSurfaceView;
import com.kantoniak.discrete_fox.scene.Map;
import com.kantoniak.discrete_fox.scene.MapRenderer;
import com.trivago.triava.util.UnitFormatter;
import com.trivago.triava.util.UnitSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    // Screens
    @BindView(R.id.overlay) ViewGroup mOverlayView;
    @BindView(R.id.screen_menu) ViewGroup mScreenMenu;
    @BindView(R.id.screen_question) ViewGroup mScreenQuestion;

    // screen_menu
    @BindView(R.id.animation_main) ImageView mAnimationMain;
    @BindView(R.id.start_button) Button mStartButton;

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

    @BindView(R.id.list_view_answers) ListView mListView;
    @BindView(R.id.answers_recycler) RecyclerView mAnswersRecycler;
    @BindView(R.id.list_view_linear_layout) LinearLayout mListViewLinearLayout;

    private Gameplay gameplay;
    boolean showingAnswers = false;
    private AnswersAdapter answersAdapter;

    // screen_score
    @BindView(R.id.score_points) TextView mScoreTextView;
    @BindView(R.id.great_job_tv) TextView mGreatJobTv;
    @BindView(R.id.score_play_again) TextView mPlayAgainButton;
    @BindView(R.id.facebook_share_button) ShareButton mShareButton;

    // Gameplay part
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAMERA_PERMISSION = 0;
    private static final String SCORE_PREFS = "score";

    @BindView(R.id.game_map_preview) GameSurfaceView gameMapPreview;

    private final EasyARController arController = new EasyARController();
    private final ViewMatrixOverrideCamera camera = new ViewMatrixOverrideCamera();
    private final Map map = new Map();
    private MapRenderer renderer;

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
        for (int i = 0; i < qc.numberOfQuestions(); i++) {
            questions.add(q[i]);
        }
        return questions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // FIXME: Look at this
        setupMenu();
        setupAnswersRecycler();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EasyARUtils.initializeEngine(this);
        // FIXME: This should be called earlier, from some other Activity.
        // FIXME: Camera will not show up the first time because device is opened earlier.
        requestCameraPermission();

        setupGameSurfaceView();
    }

    public void setupMenu() {
        Glide.with(this)
                .load(R.raw.tap)
                .into(mAnimationMain);
        showScreen(R.id.screen_menu);
    }

    public void setupAnswersRecycler() {
        mAnswersRecycler.setHasFixedSize(true);
        mAnswersRecycler.setLayoutManager(new LinearLayoutManager(this));

        List<AnswersAdapter.Answer> answers = Arrays.asList(
                //new AnswersAdapter.Answer("Polska", "300", 0xFF990000),
                //new AnswersAdapter.Answer("Niemcy", "500", 0xFFCC0000),
                //new AnswersAdapter.Answer("Hiszpania", "400", 0xFFFF0000)
        );

        answersAdapter = new AnswersAdapter(answers);
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
        // TODO mp3 final
        //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.twoj_wynik_to);
        //mp.start();
        int objFileId = getApplicationContext().getResources().getIdentifier("a" + String.valueOf(gameplay.getResult()) + "pkt", "raw", getApplicationContext().getPackageName());
        MediaPlayer mp2 = MediaPlayer.create(getApplicationContext(), objFileId);
        mp2.start();
    }

    @OnClick(R.id.start_button)
    public void startGame(View view) {
        showingAnswers = false;
        mListViewLinearLayout.setVisibility(View.INVISIBLE);
        map.disableAllCountries();
        //TODO Call generateQuestions on application start.
        gameplay = new Gameplay(generateQuestions(), 5);
        Question question = gameplay.getCurrentQuestion();
        showQuestion(question);

        showingAnswers = false;
        showScreen(R.id.screen_question);
    }

    @OnClick(R.id.button_close)
    public void buttonClose() { showScreen(R.id.screen_menu); }

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
            mListViewLinearLayout.setVisibility(View.INVISIBLE);

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
            String[] forList = new String[countries.size()];
            CountryUtil cu = new CountryUtil();
            List<AnswersAdapter.Answer> answers = new LinkedList<>();
            for (int i = 0; i < countries.size(); i++) {
                int r = currentQuestion.getCorrectAnswer(cu.convert(countries.get(i)));
                double res = currentQuestion.getAnsDouble().get(cu.convert(countries.get(i)));
                String ress = "";
                if (currentQuestion.getUnit().equals("%")) {
                    ress = String.format("%.2f", res) + currentQuestion.getUnit();
                } else {
                    ress = UnitFormatter.formatAsUnit((long) res, UnitSystem.SI, currentQuestion.getUnit());
                }
                if (countries.get(i).equals("de")) {
                    forList[i] = "Germany - " + ress;
                } else {
                    forList[i] = cu.convert(countries.get(i)) + " - " + ress;
                }

                String countryName = cu.convert(countries.get(i));
                if (countries.get(i).equals("de")) {
                    countryName = "Germany";
                }
                int height = currentQuestion.getCorrectAnswer(cu.convert(countries.get(i)));
                int color = ColorUtils.blendARGB(currentQuestion.getMminColor(), currentQuestion.getMmaxColor(), (height-1)*0.5f);
                answers.add(new AnswersAdapter.Answer(countryName, ress, res, color));
            }
            //currentQuestion.getMmaxColor();
            answers.sort(Comparator.comparing(AnswersAdapter.Answer::getValueRaw).reversed());
            answersAdapter.updateAnswers(answers);

            ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, forList);
            mListView.setAdapter(test);
            mListViewLinearLayout.setVisibility(View.VISIBLE);
            mNextIcon.setImageResource(R.drawable.ic_trending_flat_24dp);
            showingAnswers = true;
            for (String code : countries) {
                map.getCountry(code).setHeight(
                        currentQuestion.getCorrectAnswer(cu.convert(code)));
            }
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
        mRoundProgress.setText(String.valueOf(gameplay.getCurrentQuestionInt() + 1) + "/" + String.valueOf(gameplay.NUMBEROFQUESTIONS));

        mHighTextView.setText(question.getMaxLabel());
        mMidTextView.setText(question.getMidLabel());
        mLowTextView.setText(question.getMinLabel());

        int maxColor = question.getMmaxColor();
        int minColor = question.getMminColor();
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
