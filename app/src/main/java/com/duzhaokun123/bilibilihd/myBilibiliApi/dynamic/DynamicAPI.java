package com.duzhaokun123.bilibilihd.myBilibiliApi.dynamic;

import android.os.Looper;

import com.duzhaokun123.bilibilihd.myBilibiliApi.dynamic.model.DynamicPage;
import com.duzhaokun123.bilibilihd.myBilibiliApi.dynamic.model.NestedCard;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.BilibiliClientProperties;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DynamicAPI {

    private static DynamicAPI dynamicAPI;

    public static DynamicAPI getDynamicAPI() {
        if (dynamicAPI == null) {
            dynamicAPI = new DynamicAPI();
        }
        dynamicAPI.loginResponse = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getLoginResponse();
        dynamicAPI.bilibiliClientProperties = PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getBillingClientProperties();
        dynamicAPI.okHttpClient = new OkHttpClient();

        return dynamicAPI;
    }

    public static NestedCard getNestedCard(String card) {
        Gson gson = new Gson();
        return gson.fromJson(card, NestedCard.class);
    }

    private DynamicAPI() {
    }

    private LoginResponse loginResponse;
    private BilibiliClientProperties bilibiliClientProperties;
    private OkHttpClient okHttpClient;
    private Gson gson;

    public void getDynamic(int page, Callback callback) {
        Exception exception = null;
        Response response = null;

        if (loginResponse == null) {
            return;
        }
        String params = "access_key=" + loginResponse.getData().getTokenInfo().getAccessToken() +
                "&appkey=" + bilibiliClientProperties.getAppKey() +
                "&build=" + bilibiliClientProperties.getBuild() +
                "&channel=" + bilibiliClientProperties.getChannel() +
                "&from=feed" +
                "&mobi_app=android" +
                "&offset_dynamic_id=" +
                "&page=" + page +
                "&platform=" + bilibiliClientProperties.getPlatform() +
                "&qn=32" +
                "&rsp_type=2" +
                "&src=bilih5" +
                "&statistics=%7B%22appId%22%3A1%2C%22platform%22%3A3%2C%22version%22%3A%225.54.0%22%2C%22abtest%22%3A%22%22%7D" +
//               "&trace_id=20200226084100005" //好象是当前 YYYYMMDDhhmmss + 毫秒 +
                "&ts=" + System.currentTimeMillis() +
                "&type_list=268435455" + //不知道是怎么来的, 每次都一样
                "&uid=" + loginResponse.getUserId() +
                "&version=" + bilibiliClientProperties.getVersion() +
                "&video_meta=fourk%3A1%2Cfnval%3A16%2Cfnver%3A0%2Cqn%3A32";

        //这个地方加不加 appSecret 校验都是对
        String sign = OtherUtils.MD5(params  + bilibiliClientProperties.getAppSecret());

        String url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new?" + params + "&sign=" + sign;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", bilibiliClientProperties.getDefaultUserAgent())
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (Exception e) {
            exception = e;
        }

        Looper.prepare();
        if (exception == null) {
            try {
                if (gson == null) {
                    gson = new Gson();
                }
                DynamicPage dynamicPage = gson.fromJson(response.body().string(), DynamicPage.class);
                if (dynamicPage.getCode() == 0) {
                    callback.onSuccess(dynamicPage);
                } else {
                    throw new BilibiliApiException(new CommonResponse(
                            dynamicPage.getCode(),
                            dynamicPage.getMsg(),
                            dynamicPage.getMsg(),
                            System.currentTimeMillis(),
                            null,
                            dynamicPage.getTtl()
                    ));
                }
            } catch (Exception e) {
                callback.onException(e);
            }
        } else {
            callback.onException(exception);
        }
        Looper.loop();
    }

    public interface Callback {
        void onException(Exception e);

        void onSuccess(DynamicPage dynamicPage);
    }
}
