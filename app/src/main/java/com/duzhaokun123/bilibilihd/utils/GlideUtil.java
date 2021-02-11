package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideUtil {
    /**
     *
     * @deprecated 这没有必要
     */
    @Deprecated
    public static void loadUrlInto(Context context, String url, ImageView imageView, boolean autoAspectRation) {
        if (url == null) {
            imageView.setImageDrawable(null);
            return;
        }
        Glide.with(context).load(url).into(imageView);
    }
}
