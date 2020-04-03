package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;

public class UrlOpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        String host = uri.getHost();
        String path = uri.getPath();
        Log.d("UrlOpenActivity", uri.toString());

        Intent intent1 = null;
        switch (hostLooksLikeWhichType(host)) {
            case SPACE:
                intent1 = new Intent(this, UserSpaceActivity.class);
                intent1.putExtra("uid", getUidFromPath(path));
                break;
            case UNKNOWN:
                ToastUtil.sendMsg(this, "可能不支持 " + uri.toString());
                break;
        }
        if (intent1 != null) {
            startActivity(intent1);
        }
        finish();
    }

    private Type hostLooksLikeWhichType(String host) {
        if (host == null) {
            return Type.UNKNOWN;
        } else if (host.startsWith("space")) {
            return Type.SPACE;
        } else if (host.startsWith("m")) {
            return Type.M;
        } else if(host.startsWith("www")) {
            return Type.WWW;
        } else {
            return Type.UNKNOWN;
        }
    }

    private long getUidFromPath(String path) {
        int slash;
        long re = 0;
        Log.d("getUidFromPath", "input " + path);
        try {
            if ((slash = path.indexOf('/', 1)) != -1) {
                re = Long.parseLong(path.substring(1, slash));
            } else {
                re = Long.parseLong(path.substring(1));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Log.d("getUidFromPath", "output " + re);
        return re;
    }

    enum Type {
        SPACE,
        VIDEO,
        M,
        WWW,
        UNKNOWN
    }
}
