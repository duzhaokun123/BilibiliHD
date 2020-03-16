package com.duzhaokun123.bilibilihd.utils;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewUtil {
    public static void autoAspectRation(ImageView imageView, Drawable drawable) {
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = imageView.getWidth() / drawable.getIntrinsicWidth() * drawable.getIntrinsicHeight() + imageView.getPaddingBottom() + imageView.getPaddingTop();
        imageView.setLayoutParams(params);
    }
}
