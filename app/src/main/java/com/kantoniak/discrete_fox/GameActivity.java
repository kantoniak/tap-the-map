package com.kantoniak.discrete_fox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.kantoniak.discrete_fox.ar.EasyARController;
import com.kantoniak.discrete_fox.ar.EasyARRenderingDelegate;
import com.kantoniak.discrete_fox.ar.UpdateBackgroundAndMatricesCallback;
import com.kantoniak.discrete_fox.ar.ViewMatrixOverrideCamera;
import com.kantoniak.discrete_fox.game_ui.AboutMarkerFragment;
import com.kantoniak.discrete_fox.game_ui.LoadingFragment;
import com.kantoniak.discrete_fox.game_ui.QuestionSeriesFragment;
import com.kantoniak.discrete_fox.game_ui.RulesBoardFragment;
import com.kantoniak.discrete_fox.game_ui.ScanToStartFragment;
import com.kantoniak.discrete_fox.gameplay.Question;
import com.kantoniak.discrete_fox.gameplay.QuestionChest;
import com.kantoniak.discrete_fox.scene.GameSurfaceView;
import com.kantoniak.discrete_fox.scene.Map;
import com.kantoniak.discrete_fox.scene.MapRenderer;
import com.kantoniak.discrete_fox.scene.RenderingDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Activity for displaying the main game screen.
 */
public class GameActivity extends AppCompatActivity
        implements LoadingFragment.InteractionListener, RulesBoardFragment.InteractionListener, AboutMarkerFragment.InteractionListener, ScanToStartFragment.InteractionListener, QuestionSeriesFragment.InteractionListener {

    public static final String MESSAGE_SCORE = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_SCORE";
    public static final String MESSAGE_SCORE_OUT_OF = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_SCORE_OUT_OF";
    public static final String MESSAGE_IS_HIGHSCORE = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_IS_HIGHSCORE";

    private View.OnTouchListener currentTouchListener;

    @BindView(R.id.game_map_preview)
    GameSurfaceView gameMapPreview;

    private final EasyARController arController = new EasyARController();
    private final ViewMatrixOverrideCamera camera = new ViewMatrixOverrideCamera();
    private MapRenderer renderer;
    private List<Question> questionList;

    /**
     * Set up the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setupGameSurfaceView();
        questionList = new ArrayList<>();
    }

    /**
     * When activity starts.
     */
    @Override
    protected void onStart() {
        super.onStart();
        switchToLoader();
    }

    /**
     * Set up game surface view.
     */
    private void setupGameSurfaceView() {
        renderer = new MapRenderer(this, new Map());
        renderer.setCamera(camera);
        gameMapPreview.setSurfaceRenderer(renderer);

        RenderingDelegate arDelegate = new EasyARRenderingDelegate(arController);
        renderer.setRenderingDelegate(arDelegate);
        gameMapPreview.setRenderingDelegate(arDelegate);

        UpdateBackgroundAndMatricesCallback updateMatricesCallback = new UpdateBackgroundAndMatricesCallback(arController, camera);
        renderer.getCurrentScene().registerFrameCallback(updateMatricesCallback);
    }

    /**
     * Present loading bar.
     */
    public void switchToLoader() {
        LoadingFragment loadingFragment = new LoadingFragment();
        loadingFragment.init(renderer, questionList);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, loadingFragment).commit();
        this.currentTouchListener = null;
    }

    /**
     * Present rules.
     */
    public void switchToRules() {
        RulesBoardFragment rulesBoardFragment = new RulesBoardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, rulesBoardFragment).commit();
        this.currentTouchListener = null;
    }

    /**
     * Present marker scanner.
     */
    public void switchToScanner() {
        ScanToStartFragment scanFragment = new ScanToStartFragment();
        scanFragment.init(arController);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, scanFragment).commit();
        this.currentTouchListener = null;
    }

    /**
     * Present about marker information.
     */
    public void switchToAboutMarker() {
        AboutMarkerFragment aboutFragment = new AboutMarkerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, aboutFragment).commit();
        this.currentTouchListener = null;
    }

    /**
     * Present questions.
     */
    public void switchToQuestions() {
        QuestionSeriesFragment questionFragment = new QuestionSeriesFragment();
        questionFragment.init(renderer, camera, questionList);
        this.currentTouchListener = questionFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, questionFragment).commit();
        renderer.showMap(true);
    }

    /**
     * When touched.
     */
    @OnTouch(R.id.game_map_preview)
    public boolean onTouch(View view, MotionEvent event) {
        return currentTouchListener != null && currentTouchListener.onTouch(view, event);
    }

    /**
     * When activity was loaded.
     */
    @Override
    public void onLoaded() {
        if (SharedPrefsUtil.shouldShowRules(this)) {
            switchToRules();
        } else {
            switchToScanner();
        }
    }

    /**
     * Display the rules.
     * @param showRulesAgain Whether we should display the rules
     */
    @Override
    public void onRulesRead(boolean showRulesAgain) {
        if (!showRulesAgain) {
            SharedPrefsUtil.dontShowRulesAgain(this);
        }
        switchToScanner();
    }

    /**
     * Display the help.
     */
    @Override
    public void onHelpClick() {
        switchToAboutMarker();
    }

    /**
     * Display the marker scanner view.
     */
    @Override
    public void onAboutRead() {
        switchToScanner();
    }

    /**
     * When marker was found.
     */
    @Override
    public void onScanned() {
        switchToQuestions();
    }

    /**
     * Close triggered.
     */
    @Override
    public void onCloseTriggered() {
        this.finish();
    }

    /**
     * When series of questions has finished.
     * @param score Score of the series of questions
     * @param outOf Maximum of the series of questions
     */
    @Override
    public void onSeriesDone(int score, int outOf) {
        boolean isHighscore = SharedPrefsUtil.updateHighScore(this, score);

        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra(MESSAGE_SCORE, score);
        intent.putExtra(MESSAGE_SCORE_OUT_OF, outOf);
        intent.putExtra(MESSAGE_IS_HIGHSCORE, isHighscore);
        startActivity(intent);
        finish();
    }

    /**
     * Game resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (gameMapPreview != null) {
            gameMapPreview.onResume();
        }
    }

    /**
     * Game pause.
     */
    @Override
    public void onPause() {
        if (gameMapPreview != null) {
            gameMapPreview.onPause();
        }
        super.onPause();
    }

    /**
     * When pressed system back button.
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
