package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.github.chrisbanes.photoview.PhotoView;

import static android.os.Environment.DIRECTORY_PICTURES;

public class PhotoViewActivity extends AppCompatActivity {

    private PhotoView mPv;
    private ImageButton mIbPip, mIbDl;
    private RelativeLayout mRl;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        mPv = findViewById(R.id.pv);
        mIbPip = findViewById(R.id.ib_pip);
        mIbDl = findViewById(R.id.ib_dl);
        mRl = findViewById(R.id.rl);

        intent = getIntent();
        String url;
        if ((url = intent.getExtras().getString("url", null)) != null) {
            Glide.with(mPv).load(url).into(mPv);
        }

        mIbPip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rational rational = new Rational(mPv.getDrawable().getIntrinsicWidth(), mPv.getDrawable().getIntrinsicHeight());
                if (rational.doubleValue() > 0.418410 && rational.doubleValue() < 2.390000) {
                    PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                            .setAspectRatio(rational)
                            .build();
                    enterPictureInPictureMode(pictureInPictureParams);
                } else {
                    ToastUtil.sendMsg(PhotoViewActivity.this, R.string.inappropriate);
                }
            }
        });

        mIbDl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((intent.getExtras().getString("url", null)) != null) {
                    DownloadUtil.picturesDownload(PhotoViewActivity.this, intent.getExtras().getString("url"));
                }
            }
        });

        mRl.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            private boolean changed;

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                DisplayCutout displayCutout = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    displayCutout = v.getRootWindowInsets().getDisplayCutout();
                    if (displayCutout != null && !changed) {
                        changed = true;
                        v.setPadding(v.getPaddingLeft() + displayCutout.getSafeInsetLeft(),
                                v.getPaddingTop(),
                                v.getPaddingRight() + displayCutout.getSafeInsetRight(),
                                v.getPaddingBottom() + displayCutout.getSafeInsetBottom());
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            mIbDl.setImageResource(R.color.colorTransparency);
            mIbPip.setImageResource(R.color.colorTransparency);
        } else {
            mIbDl.setImageResource(R.drawable.ic_dl);
            mIbPip.setImageResource(R.drawable.ic_pip);
        }
    }
}
