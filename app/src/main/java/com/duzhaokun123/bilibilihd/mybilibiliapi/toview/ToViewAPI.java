package com.duzhaokun123.bilibilihd.mybilibiliapi.toview;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ToViewAPI {
    private static ToViewAPI toViewAPI;

    public static ToViewAPI getInstance() {
        if (toViewAPI == null) {
            toViewAPI = new ToViewAPI();
        }
        return toViewAPI;
    }

    private ToViewAPI() {}

    private PBilibiliClient pBilibiliClient = PBilibiliClient.INSTANCE;

    public void addAid(long aid, MyBilibiliClient.ICallback<CommonResponse> callback) {
        LoginResponse loginResponse = pBilibiliClient.getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        try {
            String response = MyBilibiliClient.getInstance().getResponseByPost(new MyBilibiliClient.Request() {
                @Override
                public String getUrl() {
                    return "http://api.bilibili.com/x/v2/history/toview/add";
                }

                @Override
                public void addUserParams(@NotNull Map<String, String> paramsMap) {
                    paramsMap.put("aid", String.valueOf(aid));
                }
            });
            CommonResponse commonResponse = GsonUtil.getGsonInstance().fromJson(response, CommonResponse.class);
            if (commonResponse.getCode() == 0) {
                callback.onSuccess(commonResponse);
            } else {
                throw new BilibiliApiException(commonResponse);
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }

    public void addBvid(String bvid, MyBilibiliClient.ICallback<CommonResponse> callback) {
        LoginResponse loginResponse = pBilibiliClient.getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        try {
            String response = MyBilibiliClient.getInstance().getResponseByPost(new MyBilibiliClient.Request() {
                @Override
                public String getUrl() {
                    return "http://api.bilibili.com/x/v2/history/toview/add";
                }

                @Override
                public void addUserParams(@NotNull Map<String, String> paramsMap) {
                    paramsMap.put("bvid", bvid);
                }
            });
            CommonResponse commonResponse = GsonUtil.getGsonInstance().fromJson(response, CommonResponse.class);
            if (commonResponse.getCode() == 0) {
                callback.onSuccess(commonResponse);
            } else {
                throw new BilibiliApiException(commonResponse);
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }
}
