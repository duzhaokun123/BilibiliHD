package com.duzhaokun123.bilibilihd.ui.play;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.duzhaokun123.bilibilihd.R;

public class PlayActivity extends AppCompatActivity {

    private TextView mTvAv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mTvAv = findViewById(R.id.tv_av);
        mTvAv.setText("av" + getIntent().getExtras().getString("av"));
    }
}
