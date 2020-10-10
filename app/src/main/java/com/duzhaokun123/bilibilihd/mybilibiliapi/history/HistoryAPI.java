package com.duzhaokun123.bilibilihd.mybilibiliapi.history;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HistoryAPI {

    private static HistoryAPI historyApi;

    public static HistoryAPI getInstance() {
        if (historyApi == null) {
            historyApi = new HistoryAPI();
        }
        return historyApi;
    }

    private HistoryAPI() {
    }

    public void setAidHistory(long aid, long cid, long playedTime, MyBilibiliClient.ICallback<CommonResponse> callback) {
        LoginResponse loginResponse = Application.getPBilibiliClient().getBilibiliClient().getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        try {
            String response = MyBilibiliClient.getInstance().getResponseByPost(new MyBilibiliClient.Request() {
                @Override
                public String getUrl() {
                    return "http://api.bilibili.com/x/click-interface/web/heartbeat";
                }

                @Override
                public void addUserParams(@NotNull Map<String, String> paramsMap) {
                    paramsMap.put("aid", String.valueOf(aid));
                    paramsMap.put("cid", String.valueOf(cid));
                    paramsMap.put("played_time", String.valueOf(playedTime));
                }
            });
            CommonResponse historyReport = GsonUtil.getGsonInstance().fromJson(response, CommonResponse.class);
            if (historyReport.getCode() == 0) {
                callback.onSuccess(historyReport);
            } else {
                throw new BilibiliApiException(historyReport);
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }

}
