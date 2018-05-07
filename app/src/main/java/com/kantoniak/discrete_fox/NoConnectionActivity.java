package com.kantoniak.discrete_fox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public class NoConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }
}
