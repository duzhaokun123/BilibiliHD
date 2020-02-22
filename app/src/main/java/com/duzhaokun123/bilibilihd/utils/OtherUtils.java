package com.duzhaokun123.bilibilihd.utils;

import android.widget.ImageView;

import com.duzhaokun123.bilibilihd.R;

public class OtherUtils {
    public static void setLevelDrawable(ImageView imageView, int level) {
        switch (level) {
            case 0:
                imageView.setImageResource(R.drawable.ic_user_level_0);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_user_level_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_user_level_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_user_level_3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ic_user_level_4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.ic_user_level_5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.ic_user_level_6);
                break;
        }
    }

    public static void setSixDrawable(ImageView imageView, String sex) {
        if (sex.equals("男")) {
            imageView.setImageResource(R.drawable.ic_m);
        } else if (sex.equals("女")) {
            imageView.setImageResource(R.drawable.ic_f);
        } else {
            imageView.setImageDrawable(null);
        }
    }
}
