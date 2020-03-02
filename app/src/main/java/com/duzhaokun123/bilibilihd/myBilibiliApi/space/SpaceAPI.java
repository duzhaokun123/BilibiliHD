package com.duzhaokun123.bilibilihd.myBilibiliApi.space;

import android.os.Looper;

import com.duzhaokun123.bilibilihd.myBilibiliApi.space.model.Space;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.BilibiliClientProperties;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpaceAPI {
    private static SpaceAPI spaceAPI;

    public static SpaceAPI getSpaceAPI() {
        if (spaceAPI == null) {
            spaceAPI = new SpaceAPI();
        }

        spaceAPI.loginResponse = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getLoginResponse();
        spaceAPI.bilibiliClientProperties = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getBillingClientProperties();
        spaceAPI.okHttpClient = new OkHttpClient();

        return spaceAPI;
    }

    private SpaceAPI() {}

    private LoginResponse loginResponse;
    private BilibiliClientProperties bilibiliClientProperties;
    private OkHttpClient okHttpClient;
    private Gson gson;

    public void getSpace(long uid, Callback callback) {
        Exception exception = null;
        Response response = null;

        if (loginResponse == null) {
            return;
        }

        String params = "access_key=" + loginResponse.getData().getTokenInfo().getAccessToken() +
                "&appkey=" + bilibiliClientProperties.getAppKey() +
                "&build=" + bilibiliClientProperties.getBuild() +
                "&channel=" + bilibiliClientProperties.getChannel() +
                "&from=" + 0 +
                "&mobi_app=" + "android" +
                "&platform=" + bilibiliClientProperties.getPlatform() +
                "&ps=" + 10 +
                "&ts=" + System.currentTimeMillis() +
                "&vmid=" + uid;

        String sign = OtherUtils.MD5(params + bilibiliClientProperties.getAppSecret());

        String url = "https://app.bilibili.com/x/v2/space?" + params + "&sign=" + sign;


        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", bilibiliClientProperties.getDefaultUserAgent())
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (Exception e) {
            exception = e;
        }

        Looper.prepare();
        if (exception == null) {
            try {
                if (gson == null){
                    gson = new Gson();
                }
                Space space = gson.fromJson(response.body().string(), Space.class);
                if (space.getCode() == 0) {
                    callback.onSuccess(space);
                } else {
                    throw new BilibiliApiException(new CommonResponse(
                            space.getCode(),
                            space.getMessage(),
                            space.getMessage(),
                            System.currentTimeMillis(),
                            null,
                            space.getTtl()
                    ));
                }
            } catch (Exception e) {
                callback.onException(e);
            }
        } else {
            callback.onException(exception);
        }
        Looper.loop();
    }

    public interface Callback {
        void onException(Exception e);

        void onSuccess(Space space);
    }
}
