package com.duzhaokun123.bilibilihd.mybilibiliapi.space;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.util.Map;

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

    public void getSpace(long uid, MyBilibiliClient.Callback<Space> callback) {

        if (pBilibiliClient == null) {
            pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        }
        try {
            String response = MyBilibiliClient.getMyBilibiliClient().getResponse(new MyBilibiliClient.GetRequest() {
                @Override
                public String getUrl() {
                    return "https://app.bilibili.com/x/v2/space";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("from", String.valueOf(0));
                    paramsMap.put("mobi_app", "android");
                    paramsMap.put("ps", String.valueOf(10));
                    paramsMap.put("vmid", String.valueOf(uid));
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
