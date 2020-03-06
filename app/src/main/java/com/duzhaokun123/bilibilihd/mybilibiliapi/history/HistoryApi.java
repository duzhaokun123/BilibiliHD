package com.duzhaokun123.bilibilihd.mybilibiliapi.history;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.model.History;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.BilibiliClientProperties;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.util.Map;
import java.util.TreeMap;

public class HistoryApi {

    private static HistoryApi historyApi;

    public static HistoryApi getHistoryApi() {
        if (historyApi == null) {
            historyApi = new HistoryApi();
        }
        return historyApi;
    }

    private HistoryApi() {}

    private Gson gson;

    public void getHistory(String business, MyBilibiliClient.CallBack<History> callback) {
        getHistory(0, 0, business, callback);
    }

    public void getHistory(long max, int maxTp, String business, MyBilibiliClient.CallBack<History> callback) {
        LoginResponse loginResponse = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        BilibiliClientProperties bilibiliClientProperties = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getBillingClientProperties();
        try {
            String response = MyBilibiliClient.getMyBilibiliClient().getResponse(new MyBilibiliClient.GetRequest() {
                @Override
                public String getUrl() {
                    return "https://app.bilibili.com/x/v2/history/cursor";
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> map = new TreeMap<>();
                    map.put("access_key", loginResponse.getData().getTokenInfo().getAccessToken());
                    map.put("appkey", bilibiliClientProperties.getAppKey());
                    map.put("build", bilibiliClientProperties.getBuild());
                    map.put("business", business);
                    map.put("channel", bilibiliClientProperties.getChannel());
                    map.put("max", String.valueOf(max));
                    map.put("max_tp", String.valueOf(maxTp));
                    map.put("mobi_app", "android");
                    map.put("platform", bilibiliClientProperties.getPlatform());
                    map.put("ps", String.valueOf(20));
                    map.put("ts", String.valueOf(System.currentTimeMillis()));
                    return map;
                }
            });
            if (gson == null) {
                gson = new Gson();
            }
            History history = gson.fromJson(response, History.class);
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
    }

}
