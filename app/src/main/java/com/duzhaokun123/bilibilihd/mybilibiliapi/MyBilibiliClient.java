package com.duzhaokun123.bilibilihd.mybilibiliapi;

import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.hiczp.bilibili.api.BilibiliClientProperties;

import java.io.IOException;
import java.util.Map;

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

    private MyBilibiliClient(PBilibiliClient pBilibiliClient) {
        this.pBilibiliClient = pBilibiliClient;
    }

    public String getResponse(GetRequest getRequest) throws IOException {
        BilibiliClientProperties bilibiliClientProperties = pBilibiliClient.getBilibiliClient().getBillingClientProperties();
        StringBuilder paramsSB = new StringBuilder();
        for (String key : getRequest.getParams().keySet()) {
            paramsSB.append(key)
                    .append("=")
                    .append(getRequest.getParams().get(key))
                    .append("&");
        }
        paramsSB.deleteCharAt(paramsSB.length() - 1);
        String sign = OtherUtils.MD5(paramsSB.toString() + bilibiliClientProperties.getAppSecret());
        StringBuilder urlSB = new StringBuilder(getRequest.getUrl())
                .append("?")
                .append(paramsSB)
                .append("&sign=")
                .append(sign);
        Request request = new Request.Builder()
                .url(urlSB.toString())
                .addHeader("User-Agent", bilibiliClientProperties.getDefaultUserAgent())
                .build();

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        return okHttpClient.newCall(request).execute().body().string();
    }

    public interface GetRequest {
        String getUrl();

        Map<String, String> getParams();
    }

    public interface CallBack<T> {
        void onException(Exception e);

        void onSuccess(T t);
    }
}
