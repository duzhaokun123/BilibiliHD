package com.duzhaokun123.bilibilihd.mybilibiliapi.toview;

import com.duzhaokun123.bilibilihd.model.BilibiliWebCookie;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.model.Base;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

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

    private PBilibiliClient pBilibiliClient = PBilibiliClient.Companion.getInstance();

    public void addAid(long aid, MyBilibiliClient.ICallback<Base> callback) {
        LoginResponse loginResponse = pBilibiliClient.getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        BilibiliWebCookie bilibiliWebCookie = MyBilibiliClientUtil.getBilibiliWebCookie(loginResponse);
        try {
            String response = MyBilibiliClient.getInstance().getResponseByPost(new MyBilibiliClient.Request() {
                @Override
                public String getUrl() {
                    return "http://api.bilibili.com/x/v2/history/toview/add";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("aid", String.valueOf(aid));
                    paramsMap.put("csrf", bilibiliWebCookie.getBiliJct());
                }
            }, bilibiliWebCookie.getSessdata());
            Base base = GsonUtil.getGsonInstance().fromJson(response, Base.class);
            if (base.getCode() == 0) {
                callback.onSuccess(base);
            } else {
                throw new BilibiliApiException(new CommonResponse(
                        base.getCode(),
                        base.getMessage(),
                        base.getMessage(),
                        System.currentTimeMillis(),
                        null,
                        base.getTtl()
                ));
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }

    public void addBvid(String bvid, MyBilibiliClient.ICallback<Base> callback) {
        LoginResponse loginResponse = pBilibiliClient.getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        BilibiliWebCookie bilibiliWebCookie = MyBilibiliClientUtil.getBilibiliWebCookie(loginResponse);
        try {
            String response = MyBilibiliClient.getInstance().getResponseByPost(new MyBilibiliClient.Request() {
                @Override
                public String getUrl() {
                    return "http://api.bilibili.com/x/v2/history/toview/add";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("bvid", bvid);
                    paramsMap.put("csrf", bilibiliWebCookie.getBiliJct());
                }
            }, bilibiliWebCookie.getSessdata());
            Base base = GsonUtil.getGsonInstance().fromJson(response, Base.class);
            if (base.getCode() == 0) {
                callback.onSuccess(base);
            } else {
                throw new BilibiliApiException(new CommonResponse(
                        base.getCode(),
                        base.getMessage(),
                        base.getMessage(),
                        System.currentTimeMillis(),
                        null,
                        base.getTtl()
                ));
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }
}
