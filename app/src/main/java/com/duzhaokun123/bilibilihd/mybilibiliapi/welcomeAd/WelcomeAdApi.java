package com.duzhaokun123.bilibilihd.mybilibiliapi.welcomeAd;

import androidx.annotation.NonNull;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.welcomeAd.model.WelcomeAd;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.util.Map;
import java.util.Random;

public class WelcomeAdApi {

    private static WelcomeAdApi welcomeAdApi;

    public static WelcomeAdApi getInstance() {
        if (welcomeAdApi == null) {
            welcomeAdApi = new WelcomeAdApi();
        }
        return welcomeAdApi;
    }

    public static WelcomeAd.Data.List_ getShowList(@NonNull WelcomeAd welcomeAd) {
        if (welcomeAd.getData().getList() == null) {
            return null;
        }

        if (welcomeAd.getData().getShow() == null) { //没有 show 就随机来一个
            Random random = new Random(System.currentTimeMillis());
            for (WelcomeAd.Data.List_ list_ : welcomeAd.getData().getList()) {
                if (random.nextBoolean()) {
                    return list_;
                }
            }

            return null;
        }

        for (WelcomeAd.Data.Show show : welcomeAd.getData().getShow()) {
            for (WelcomeAd.Data.List_ list_ : welcomeAd.getData().getList()) {
                if (list_.getId() == show.getId()) {
                    return list_;
                }
            }
        }

        return null;
    }

    private WelcomeAdApi() {
    }

    public void getWelcomeAd(MyBilibiliClient.ICallback<WelcomeAd> callback) {
        try {
            String response = MyBilibiliClient.getInstance().getResponseByGet(new MyBilibiliClient.GetRequest() {
                @Override
                public String getUrl() {
                    return "https://app.bilibili.com/x/v2/splash/list";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("appkey", "1d8b6e7d45233436");
                    paramsMap.put("build", "5570300");
                    paramsMap.put("width", "1080");
                    paramsMap.put("height", "2048");
                    paramsMap.put("channel", "bili");
                    paramsMap.put("mobi_app", "android");
                }

            });
            WelcomeAd welcomeAd = GsonUtil.getGsonInstance().fromJson(response, WelcomeAd.class);
            if (welcomeAd.getCode() == 0) {
                callback.onSuccess(welcomeAd);
            } else {
                throw new BilibiliApiException(new CommonResponse(
                        welcomeAd.getCode(),
                        welcomeAd.getMessage(),
                        welcomeAd.getMessage(),
                        System.currentTimeMillis(),
                        null,
                        welcomeAd.getTtl()
                ));
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }
}
