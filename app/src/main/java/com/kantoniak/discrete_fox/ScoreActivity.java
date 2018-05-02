package com.kantoniak.discrete_fox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScoreActivity extends AppCompatActivity {

    @BindView(R.id.score_image)
    ImageView mScoreImage;
    @BindView(R.id.score_points)
    TextView mScorePointsTextView;
    @BindView(R.id.score_title)
    TextView mScoreTitleTextView;
    @BindView(R.id.button_facebook_share)
    ShareButton mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int result = intent.getIntExtra(GameActivity.MESSAGE_SCORE, 0);
        int maxResult = intent.getIntExtra(GameActivity.MESSAGE_SCORE_OUT_OF, 0);
        boolean newHighscore = intent.getBooleanExtra(GameActivity.MESSAGE_IS_HIGHSCORE, false);

        setupUI(result, maxResult, newHighscore);
    }

    private void setupUI(int result, int maxResult, boolean newHighscore) {

        int textToShow;
        if (newHighscore) {
            textToShow = R.string.new_high_score;
            mScoreImage.setImageResource(R.drawable.ic_trophy);
        } else if ((result * 1.0) / maxResult < 0.2f) {
            textToShow = R.string.score_needs_improvement;
            mScoreImage.setVisibility(View.GONE);
        } else {
            textToShow = R.string.score_great_job;
            mScoreImage.setImageResource(R.drawable.ic_good_job);
        }

        mScoreTitleTextView.setText(getResources().getString(textToShow));
        mScorePointsTextView.setText(getString(R.string.score_value, result, maxResult));

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setQuote("Whatever")
                .setContentUrl(Uri.parse("https://stat.gov.pl/"))
                .build();
        mShareButton.setShareContent(content);

        // TODO(kedzior) mp3 final
        //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.twoj_wynik_to);
        //mp.start();
//        try {
//            int objFileId = getApplicationContext().getResources().getIdentifier("a" + String.valueOf(GameActivity.MESSAGE_SCORE) + "pkt", "raw", BuildConfig.APPLICATION_ID);
//            MediaPlayer mp2 = MediaPlayer.create(getApplicationContext(), objFileId);
//            mp2.start();
//        } catch (Exception e) {
        // Warning: empty catch block
//        }
    }

    @OnClick(R.id.button_play_again)
    public void restartGame(View view) {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }
}
