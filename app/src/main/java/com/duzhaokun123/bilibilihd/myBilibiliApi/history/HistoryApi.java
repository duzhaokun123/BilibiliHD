package com.duzhaokun123.bilibilihd.myBilibiliApi.history;

import android.os.Looper;

import com.duzhaokun123.bilibilihd.myBilibiliApi.history.model.History;
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

public class HistoryApi {

    private static HistoryApi historyApi;

    public static HistoryApi getHistoryApi() {
        if (historyApi == null) {
            historyApi = new HistoryApi();
        }

        historyApi.loginResponse = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getLoginResponse();
        historyApi.bilibiliClientProperties = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getBillingClientProperties();
        historyApi.okHttpClient = new OkHttpClient();

        return historyApi;
    }

    private HistoryApi() {}

    private LoginResponse loginResponse;
    private BilibiliClientProperties bilibiliClientProperties;
    private OkHttpClient okHttpClient;
    private Gson gson;

    public void getHistory(String business, Callback callback) {
        getHistory(0, 0, business, callback);
    }

    public void getHistory(long max, int maxTp, String business, Callback callback) {
        Exception exception = null;
        Response response = null;

        if (loginResponse == null) {
            return;
        }

        String params = "access_key=" + loginResponse.getData().getTokenInfo().getAccessToken() +
                "&appkey=" + bilibiliClientProperties.getAppKey() +
                "&build=" + bilibiliClientProperties.getBuild() +
                "&business=" + business +
                "&channel=" + bilibiliClientProperties.getChannel() +
                "&max=" + max +
                "&max_tp=" + maxTp +
                "&mobi_app=android" +
                "&platform=" + bilibiliClientProperties.getPlatform() +
                "&ps=20" +
//                "&statistics=%7B%22appId%22%3A1%2C%22platform%22%3A3%2C%22version%22%3A%225.54.0%22%2C%22abtest%22%3A%22%22%7D" +
                "&ts=" + System.currentTimeMillis();

        String sign = OtherUtils.MD5(params + bilibiliClientProperties.getAppSecret());

        String url = "https://app.bilibili.com/x/v2/history/cursor?" + params + "&sign=" + sign;

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
                if (gson == null) {
                    gson = new Gson();
                }
                History history = gson.fromJson(response.body().string(), History.class);
                if (history.getCode() == 0) {
                    callback.onSuccess(history);
                } else {
                    throw new BilibiliApiException(new CommonResponse(
                            history.getCode(),
                            history.getMessage(),
                            history.getMessage(),
                            System.currentTimeMillis(),
                            null,
                            history.getTtl()
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

        void onSuccess(History history);
    }
}
