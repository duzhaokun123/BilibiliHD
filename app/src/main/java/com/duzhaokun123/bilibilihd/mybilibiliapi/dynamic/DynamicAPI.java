package com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model.DynamicPage;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model.NestedCard;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.BilibiliClientProperties;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.util.Map;
import java.util.TreeMap;

public class DynamicAPI {

    private static DynamicAPI dynamicAPI;

    public static DynamicAPI getDynamicAPI() {
        if (dynamicAPI == null) {
            dynamicAPI = new DynamicAPI();
        }
        return dynamicAPI;
    }

    public static NestedCard getNestedCard(String card) {
        Gson gson = new Gson();
        return gson.fromJson(card, NestedCard.class);
    }

    private DynamicAPI() {
    }

    private PBilibiliClient pBilibiliClient;
    private Gson gson;

    public void getDynamic(int page, MyBilibiliClient.CallBack<DynamicPage> callback) {
        if (pBilibiliClient == null) {
            pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        }
        LoginResponse loginResponse = pBilibiliClient.getBilibiliClient().getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        BilibiliClientProperties bilibiliClientProperties = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getBillingClientProperties();
        try {
            String response = MyBilibiliClient.getMyBilibiliClient().getResponse(new MyBilibiliClient.GetRequest() {
                @Override
                public String getUrl() {
                    return "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new";
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> map = new TreeMap<>();
                    map.put("access_key", loginResponse.getData().getTokenInfo().getAccessToken());
                    map.put("appkey", bilibiliClientProperties.getAppKey());
                    map.put("build", bilibiliClientProperties.getBuild());
                    map.put("from", "feed");
                    map.put("mobi_app", "android");
                    map.put("offset_dynamic_id", "");
                    map.put("page", String.valueOf(page));
                    map.put("qn", "32");
                    map.put("rsp_type", "2");
                    map.put("src", "bilih5");
                    map.put("statistics", "%7B%22appId%22%3A1%2C%22platform%22%3A3%2C%22version%22%3A%225.54.0%22%2C%22abtest%22%3A%22%22%7D");
                    map.put("ts", String.valueOf(System.currentTimeMillis()));
                    map.put("type_list", "268435455");
                    map.put("uid", String.valueOf(loginResponse.getUserId()));
                    map.put("version", bilibiliClientProperties.getVersion());
                    map.put("video_meta", "fourk%3A1%2Cfnval%3A16%2Cfnver%3A0%2Cqn%3A32");
                    return map;
                }
            });
            if (gson == null) {
                gson = new Gson();
            }
            DynamicPage dynamicPage = gson.fromJson(response, DynamicPage.class);
            if (dynamicPage.getCode() != 0) {
                throw new BilibiliApiException(new CommonResponse(
                        dynamicPage.getCode(),
                        dynamicPage.getMsg(),
                        dynamicPage.getMsg(),
                        System.currentTimeMillis(),
                        null,
                        dynamicPage.getTtl()
                ));
            }
            callback.onSuccess(dynamicPage);
        } catch (Exception e) {
            callback.onException(e);
        }

    }


}
