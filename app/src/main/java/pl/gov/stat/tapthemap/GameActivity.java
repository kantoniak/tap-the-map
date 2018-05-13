package pl.gov.stat.tapthemap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.gov.stat.tapthemap.ar.EasyARController;
import pl.gov.stat.tapthemap.ar.EasyARRenderingDelegate;
import pl.gov.stat.tapthemap.ar.UpdateBackgroundAndMatricesCallback;
import pl.gov.stat.tapthemap.ar.ViewMatrixOverrideCamera;
import pl.gov.stat.tapthemap.game_ui.AboutMarkerFragment;
import pl.gov.stat.tapthemap.game_ui.LoadingFragment;
import pl.gov.stat.tapthemap.game_ui.QuestionSeriesFragment;
import pl.gov.stat.tapthemap.game_ui.RulesBoardFragment;
import pl.gov.stat.tapthemap.game_ui.ScanToStartFragment;
import pl.gov.stat.tapthemap.gameplay.Question;
import pl.gov.stat.tapthemap.scene.GameSurfaceOnTouchLister;
import pl.gov.stat.tapthemap.scene.GameSurfaceView;
import pl.gov.stat.tapthemap.scene.Map;
import pl.gov.stat.tapthemap.scene.MapRenderer;
import pl.gov.stat.tapthemap.scene.RenderingDelegate;

/**
 * Activity for displaying the main game screen.
 */
public class GameActivity extends AppCompatActivity
        implements LoadingFragment.InteractionListener, RulesBoardFragment.InteractionListener, AboutMarkerFragment.InteractionListener, ScanToStartFragment.InteractionListener, QuestionSeriesFragment.InteractionListener {

    public static final String MESSAGE_SCORE = "pl.gov.stat.tapthemap.GameActivity.MESSAGE_SCORE";
    public static final String MESSAGE_SCORE_OUT_OF = "pl.gov.stat.tapthemap.GameActivity.MESSAGE_SCORE_OUT_OF";
    public static final String MESSAGE_IS_HIGHSCORE = "pl.gov.stat.tapthemap.GameActivity.MESSAGE_IS_HIGHSCORE";

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

        boolean mapVertical = SharedPrefsUtil.isMapVertical(this);
        UpdateBackgroundAndMatricesCallback updateMatricesCallback = new UpdateBackgroundAndMatricesCallback(arController, renderer, camera, mapVertical);
        renderer.getCurrentScene().registerFrameCallback(updateMatricesCallback);

        gameMapPreview.setOnTouchListener(new GameSurfaceOnTouchLister(this, renderer, camera));
    }

    /**
     * Present loading bar.
     */
    public void switchToLoader() {
        LoadingFragment loadingFragment = new LoadingFragment();
        loadingFragment.init(renderer, questionList);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, loadingFragment).commit();
    }

    /**
     * Present rules.
     */
    public void switchToRules() {
        RulesBoardFragment rulesBoardFragment = new RulesBoardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, rulesBoardFragment).commit();
    }

    /**
     * Present marker scanner.
     */
    public void switchToScanner() {
        ScanToStartFragment scanFragment = new ScanToStartFragment();
        scanFragment.init(arController);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, scanFragment).commit();
    }

    /**
     * Present about marker information.
     */
    public void switchToAboutMarker() {
        AboutMarkerFragment aboutFragment = new AboutMarkerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, aboutFragment).commit();
    }

    /**
     * Present questions.
     */
    public void switchToQuestions() {
        QuestionSeriesFragment questionFragment = new QuestionSeriesFragment();
        questionFragment.init(renderer, camera, questionList);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, questionFragment, "QuestionSeriesFragment").commit();
        renderer.showMap(true);
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
     *
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
     *
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
