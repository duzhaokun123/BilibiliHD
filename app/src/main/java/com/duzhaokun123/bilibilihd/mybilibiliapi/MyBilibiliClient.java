package com.duzhaokun123.bilibilihd.mybilibiliapi;

import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.hiczp.bilibili.api.BilibiliClientProperties;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyBilibiliClient {
    private static MyBilibiliClient myBilibiliClient;

    public static MyBilibiliClient getMyBilibiliClient() {
        if (myBilibiliClient == null) {
            myBilibiliClient = new MyBilibiliClient(PBilibiliClient.Companion.getPBilibiliClient());
        }
        return myBilibiliClient;
    }

    private PBilibiliClient pBilibiliClient;
    private OkHttpClient okHttpClient;
    private BilibiliClientProperties bilibiliClientProperties;
    private LoginResponse loginResponse;

    private MyBilibiliClient(PBilibiliClient pBilibiliClient) {
        this.pBilibiliClient = pBilibiliClient;
    }

    public String getResponse(GetRequest getRequest) throws IOException {
        bilibiliClientProperties = pBilibiliClient.getBilibiliClient().getBillingClientProperties();
        loginResponse = pBilibiliClient.getBilibiliClient().getLoginResponse();
        StringBuilder paramsSB = new StringBuilder();
        Map<String, String> paramsMap = new TreeMap<>();
        addBaseParams(paramsMap);
        getRequest.addUserParams(paramsMap);
        for (String key : paramsMap.keySet()) {
            paramsSB.append(key)
                    .append("=")
                    .append(paramsMap.get(key))
                    .append("&");
        }
        paramsSB.deleteCharAt(paramsSB.length() - 1);
        String sign = OtherUtils.MD5(paramsSB.toString() + bilibiliClientProperties.getAppSecret());
        String urlSB = getRequest.getUrl() +
                "?" +
                paramsSB +
                "&sign=" +
                sign;
        Request request = new Request.Builder()
                .url(urlSB)
                .addHeader("User-Agent", bilibiliClientProperties.getDefaultUserAgent())
                .build();

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        return okHttpClient.newCall(request).execute().body().string();
    }

    private void addBaseParams(Map<String, String> paramsMap) {
        if (loginResponse != null) {
            paramsMap.put("access_key", loginResponse.getData().getTokenInfo().getAccessToken());
        }
        paramsMap.put("appkey", bilibiliClientProperties.getAppKey());
        paramsMap.put("build", bilibiliClientProperties.getBuild());
        paramsMap.put("channel", bilibiliClientProperties.getChannel());
        paramsMap.put("platform", bilibiliClientProperties.getPlatform());
        paramsMap.put("ts", String.valueOf(System.currentTimeMillis()));
    }

    public interface GetRequest {
        String getUrl();

        void addUserParams(Map<String, String> paramsMap);
    }

    public interface CallBack<T> {
        void onException(Exception e);

        void onSuccess(T t);
    }
}
