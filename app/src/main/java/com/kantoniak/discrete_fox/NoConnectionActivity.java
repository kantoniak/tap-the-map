package com.kantoniak.discrete_fox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_go_back)
    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
