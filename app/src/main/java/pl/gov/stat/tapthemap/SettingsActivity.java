package pl.gov.stat.tapthemap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity for displaying the about screen.
 */
public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.map_vertical) ToggleSwitch mToggleSwitch;

    /**
     * Set up activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mToggleSwitch.setCheckedTogglePosition(SharedPrefsUtil.isMapVertical(this) ? 1 : 0);

        mToggleSwitch.setOnToggleSwitchChangeListener((position, isChecked) -> {
            SharedPrefsUtil.setMapVertical(this, mToggleSwitch.getCheckedTogglePosition() > 0);
        });
    }

    /**
     * When pressed go back button.
     */
    @OnClick(R.id.button_go_back)
    public void onClickBack(View view) {
        onBackPressed();
    }

    /**
     * When pressed system back button.
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
