package com.kantoniak.discrete_fox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.kantoniak.discrete_fox.game_ui.QuestionSeriesFragment;
import com.kantoniak.discrete_fox.scene.GameSurfaceView;
import com.kantoniak.discrete_fox.scene.Map;
import com.kantoniak.discrete_fox.scene.MapRenderer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;


public class GameActivity extends AppCompatActivity implements QuestionSeriesFragment.InteractionListener {

    public static final String MESSAGE_SCORE = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_SCORE";
    public static final String MESSAGE_SCORE_OUT_OF = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_SCORE_OUT_OF";
    public static final String MESSAGE_IS_HIGHSCORE = "com.kantoniak.discrete_fox.GameActivity.MESSAGE_IS_HIGHSCORE";

    private static final String SCORE_PREFS = "score";

    private View.OnTouchListener currentTouchListener;

    @BindView(R.id.game_map_preview) GameSurfaceView gameMapPreview;
    private MapRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setupRenderer();
        switchToQuestions();
    }

    public void setupRenderer() {
        renderer = new MapRenderer(this, new Map());
        gameMapPreview.setSurfaceRenderer(renderer);
    }

    public void switchToQuestions() {
        QuestionSeriesFragment questionFragment = new QuestionSeriesFragment();
        questionFragment.init(renderer);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, questionFragment).commit();
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

    @OnTouch(R.id.game_map_preview)
    public boolean onTouch(View view, MotionEvent event) {
        if (currentTouchListener != null) {
            return currentTouchListener.onTouch(view, event);
        }
        return false;
    }

    @Override
    public void onCloseTriggered() {
        startActivity(new Intent(this, MainMenuActivity.class));
        this.finish();
    }

    @Override
    public void onSeriesDone(int score, int outOf) {
        boolean isHighscore = updateHighScore(score);

        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra(MESSAGE_SCORE, score);
        intent.putExtra(MESSAGE_SCORE_OUT_OF, outOf);
        intent.putExtra(MESSAGE_IS_HIGHSCORE, isHighscore);
        startActivity(intent);
        finish();
    }
}
