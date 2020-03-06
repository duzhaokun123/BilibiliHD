package com.duzhaokun123.bilibilihd.mybilibiliapi.space;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.BilibiliClientProperties;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.util.Map;
import java.util.TreeMap;

public class SpaceAPI {
    private static SpaceAPI spaceAPI;

    public static SpaceAPI getSpaceAPI() {
        if (spaceAPI == null) {
            spaceAPI = new SpaceAPI();
        }
        return spaceAPI;
    }

    private SpaceAPI() {}


    private PBilibiliClient pBilibiliClient;
    private Gson gson;

    public void getSpace(long uid, MyBilibiliClient.CallBack<Space> callback) {

        if (pBilibiliClient == null) {
            pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        }
        BilibiliClientProperties bilibiliClientProperties = pBilibiliClient.getBilibiliClient().getBillingClientProperties();
        try {
            String response = MyBilibiliClient.getMyBilibiliClient().getResponse(new MyBilibiliClient.GetRequest() {
                @Override
                public String getUrl() {
                    return "https://app.bilibili.com/x/v2/space";
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> map = new TreeMap<>();
                    if (PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().isLogin()) {
                        map.put("access_key", pBilibiliClient.getBilibiliClient().getLoginResponse().getData().getTokenInfo().getAccessToken());
                    }
                    map.put("appkey", bilibiliClientProperties.getAppKey());
                    map.put("build", bilibiliClientProperties.getBuild());
                    map.put("channel", bilibiliClientProperties.getChannel());
                    map.put("from", String.valueOf(0));
                    map.put("mobi_app", "android");
                    map.put("platform", bilibiliClientProperties.getPlatform());
                    map.put("ps", String.valueOf(10));
                    map.put("ts", String.valueOf(System.currentTimeMillis()));
                    map.put("vmid", String.valueOf(uid));
                    return map;
                }
            });
            if (gson == null) {
                gson = new Gson();
            }
            Space space = gson.fromJson(response, Space.class);
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
    }

}
