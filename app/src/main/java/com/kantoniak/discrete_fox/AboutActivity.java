package com.kantoniak.discrete_fox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_licenses)
    public void onClickLicenses(View view) {
        startActivity(new Intent(this, LicensesActivity.class));
        finish();
    }

    @OnClick(R.id.button_go_back)
    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }
}
