package com.duzhaokun123.bilibilihd.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.article.ArticleActivity;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.utils.NotificationUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class UrlOpenActivity extends AppCompatActivity {
    private PBilibiliClient pBilibiliClient;
    private String TAG = "UrlOpenActivity";
    private boolean wait = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pBilibiliClient = PBilibiliClient.Companion.getInstance();
        if (Settings.isUninited()) {
            wait = true;
            new Thread() {
                @Override
                public void run() {
                    Settings.init(getApplicationContext());
                    LoginResponse loginResponse = Settings.getLoginUserInfoMap().getLoggedLoginResponse();
                    pBilibiliClient.getBilibiliClient().setLoginResponse(loginResponse);
                    BrowserUtil.syncLoggedLoginResponse();
//                    Config.enableLogCallback(message -> Log.d(Config.TAG, message.getText()));
                    if (Settings.isFirstStart()) {
                        NotificationUtil.init(getApplicationContext());
                        Settings.setFirstStart(false);
                    }
                    AppCompatDelegate.setDefaultNightMode(Settings.layout.getUiMode());
                }
            }.start();
        }
        Intent intent = getIntent();
        Uri uri = intent.getData();
        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        Log.d(TAG, uri.toString());
        Log.d(TAG, "scheme: " + scheme);
        Log.d(TAG, "host: " + host);
        Log.d(TAG, "path: " + path);
        Intent intent1 = null;
        if (!"bilibili".equals(scheme)) {
            switch (hostLooksLikeWhichType(host)) {
                case SPACE:
                    intent1 = new Intent(this, UserSpaceActivity.class);
                    intent1.putExtra("uid", getUidFromPath(path));
                    break;
                case WWW:
                case M:
                    switch (pathLooksLikeWhichType(path)) {
                        case READ_MOBILE:
                            intent1 = new Intent(this, ArticleActivity.class);
                            if (path != null) {
                                intent1.putExtra("id", Long.parseLong(path.substring(13)));
                            }
                            break;
                        case READ:
                            intent1 = new Intent(this, ArticleActivity.class);
                            if (path != null) {
                                intent1.putExtra("id", Long.parseLong(path.substring(8)));
                            }
                            break;
                        case VIDEO:
                            intent1 = new Intent(this, PlayActivity.class);
                            if (path != null) {
                                try {
                                    intent1.putExtra("aid", MyBilibiliClientUtil.bv2av(path.substring(7)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    intent1.putExtra("aid", Long.parseLong(path.substring(9)));
                                }
                            }
                            break;
                        case UNKNOWN:
                            ToastUtil.sendMsg(this, "可能不支持 " + uri.toString());
                            break;
                    }
                    break;
                case UNKNOWN:
                    ToastUtil.sendMsg(this, "可能不支持 " + uri.toString());
                    break;
            }
        } else {
            if ("video".equals(host)) {
                intent1 = new Intent(this, PlayActivity.class);
                if (path != null) {
                    intent1.putExtra("aid", Long.parseLong(path.substring(1)));
                }
            } else if ("article".equals(host)) {
                intent1 = new Intent(this, ArticleActivity.class);
                if (path != null) {
                    intent1.putExtra("id", Long.parseLong(path.substring(1)));
                }
            } else if ("space".equals(host)) {
                intent1 = new Intent(this, UserSpaceActivity.class);
                if (path != null) {
                    intent1.putExtra("uid", Long.parseLong(path.substring(1)));
                }
            } else  {
                ToastUtil.sendMsg(this, "可能不支持 " + uri.toString());
            }
        }
        if (intent1 != null) {
            if (wait) {
                new Handler().postAtTime(() -> startActivity(intent), 100);
            } else {
                startActivity(intent1);
            }
        }
        finish();
    }

    private Type hostLooksLikeWhichType(@Nullable String host) {
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

    private Type pathLooksLikeWhichType(@Nullable String path) {
        if (path == null) {
            return Type.UNKNOWN;
        } else if (path.startsWith("/read/mobile")) {
            return Type.READ_MOBILE;
        } else if (path.startsWith("/read/cv")) {
            return Type.READ;
        } else if (path.startsWith("/video")) {
            return Type.VIDEO;
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
        ARTICLE,
        READ,
        READ_MOBILE,
        UNKNOWN
    }
}
