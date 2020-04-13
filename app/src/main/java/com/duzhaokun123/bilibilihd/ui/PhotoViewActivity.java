package com.duzhaokun123.bilibilihd.ui;

import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Rational;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityPhotoViewBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;

public class PhotoViewActivity extends BaseActivity<ActivityPhotoViewBinding> {
    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    protected int initConfig() {
        return FULLSCREEN;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_photo_view;
    }

    @Override
    protected void initView() {
        baseBind.ibPip.setOnClickListener(v -> {
            Rational rational = new Rational(baseBind.pv.getDrawable().getIntrinsicWidth(), baseBind.pv.getDrawable().getIntrinsicHeight());
            if (rational.doubleValue() > 0.418410 && rational.doubleValue() < 2.390000) {
                PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                        .setAspectRatio(rational)
                        .build();
                enterPictureInPictureMode(pictureInPictureParams);
            } else {
                ToastUtil.sendMsg(PhotoViewActivity.this, R.string.inappropriate);
            }
        });

        baseBind.ibDl.setOnClickListener(v ->{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        DownloadUtil.downloadPicture(PhotoViewActivity.this, teleportIntent.getExtras().getString("url"));
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantResults -> {
                            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                if (teleportIntent != null && teleportIntent.getExtras() != null) {
                                    DownloadUtil.downloadPicture(PhotoViewActivity.this, teleportIntent.getExtras().getString("url"));
                                }
                            } else {
                                ToastUtil.sendMsg(PhotoViewActivity.this, getString(R.string.request) + " " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                        });
                    }
                }
        );
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            baseBind.rl.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

                private boolean changed;

                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    DisplayCutout displayCutout = v.getRootWindowInsets().getDisplayCutout();
                    if (displayCutout != null && !changed) {
                        changed = true;
                        v.setPadding(v.getPaddingLeft() + displayCutout.getSafeInsetLeft(),
                                v.getPaddingTop(),
                                v.getPaddingRight() + displayCutout.getSafeInsetRight(),
                                v.getPaddingBottom() + displayCutout.getSafeInsetBottom());
                    }
                }
            });
        }
    }

    @Override
    protected void initData() {
        String url;
        if (teleportIntent != null
                && teleportIntent.getExtras() != null
                && (url = teleportIntent.getExtras().getString("url", null)) != null) {
            Glide.with(this).load(url).into(baseBind.pv);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            baseBind.ibDl.setVisibility(View.INVISIBLE);
            baseBind.ibPip.setVisibility(View.INVISIBLE);
        } else {
            baseBind.ibDl.setVisibility(View.VISIBLE);
            baseBind.ibPip.setVisibility(View.VISIBLE);
        }
    }
}
