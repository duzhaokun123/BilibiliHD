package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Rational;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {

    private PhotoView mPv;
    private ImageButton mIbPip;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        mPv = findViewById(R.id.pv);
        mIbPip = findViewById(R.id.ib_pip);

        intent = getIntent();
        String url;
        if ((url = intent.getExtras().getString("url", null)) != null) {
            Glide.with(mPv).load(url).into(mPv);
        }

        mIbPip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                        .setAspectRatio(new Rational(mPv.getDrawable().getIntrinsicHeight(), mPv.getDrawable().getIntrinsicWidth()))
                        .build();
                enterPictureInPictureMode(pictureInPictureParams);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
