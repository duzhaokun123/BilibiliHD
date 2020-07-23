package com.duzhaokun123.bilibilihd.mybilibiliapi;

import androidx.annotation.NonNull;

import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.hiczp.bilibili.api.BilibiliClientProperties;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MyBilibiliClient {
    public static final MediaType X_WWW_FROM_URLENCODED = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");
    private static MyBilibiliClient myBilibiliClient;

    public static MyBilibiliClient getInstance() {
        if (myBilibiliClient == null) {
            myBilibiliClient = new MyBilibiliClient(PBilibiliClient.Companion.getInstance());
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

    public String getResponseByGet(Request getRequest) throws IOException {
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
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(urlSB)
                .addHeader("User-Agent", bilibiliClientProperties.getDefaultUserAgent())
                .build();

        return getOkHttpClient().newCall(request).execute().body().string();
    }

    public String getResponseByPost(Request getRequest, String sessdata) throws IOException {
        bilibiliClientProperties = PBilibiliClient.Companion.getInstance().getBilibiliClient().getBillingClientProperties();
        StringBuilder paramsSB = new StringBuilder();
        Map<String, String> paramsMap = new TreeMap<>();
        getRequest.addUserParams(paramsMap);
        for (String key : paramsMap.keySet()) {
            paramsSB.append(key)
                    .append("=")
                    .append(paramsMap.get(key))
                    .append("&");
        }
        paramsSB.deleteCharAt(paramsSB.length() - 1);

        RequestBody requestBody = RequestBody.create(paramsSB.toString(), X_WWW_FROM_URLENCODED);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(getRequest.getUrl())
                .post(requestBody)
                .addHeader("User-Agent", bilibiliClientProperties.getDefaultUserAgent())
                .addHeader("Cookie", "SESSDATA=" + sessdata)
                .build();

        return getOkHttpClient().newCall(request).execute().body().string();
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

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    public interface Request {
        String getUrl();

        void addUserParams(Map<String, String> paramsMap);
    }

    public interface ICallback<T> {
        void onException(@NonNull Exception e);

        void onSuccess(@NonNull T t);
    }
}
