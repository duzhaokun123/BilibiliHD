package com.duzhaokun123.bilibilihd.mybilibiliapi.history;

import com.duzhaokun123.bilibilihd.model.BilibiliWebCookie;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.model.Heartbeat;
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.model.History;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.util.Map;

public class HistoryApi {

    private static HistoryApi historyApi;

    public static HistoryApi getInstance() {
        if (historyApi == null) {
            historyApi = new HistoryApi();
        }
        return historyApi;
    }

    private HistoryApi() {
    }

    public void getHistory(String business, MyBilibiliClient.ICallback<History> callback) {
        getHistory(0, 0, business, callback);
    }

    public void getHistory(long max, int maxTp, String business, MyBilibiliClient.ICallback<History> callback) {
        LoginResponse loginResponse = PBilibiliClient.Companion.getInstance().getBilibiliClient().getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        try {
            String response = MyBilibiliClient.getInstance().getResponseByGet(new MyBilibiliClient.Request() {
                @Override
                public String getUrl() {
                    return "https://app.bilibili.com/x/v2/history/cursor";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("business", business);
                    paramsMap.put("max", String.valueOf(max));
                    paramsMap.put("max_tp", String.valueOf(maxTp));
                    paramsMap.put("ps", String.valueOf(20));
                }

            });
            History history = GsonUtil.getGsonInstance().fromJson(response, History.class);
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

    public void setAidHistory(long aid, long cid, long playedTime, MyBilibiliClient.ICallback<Heartbeat> callback) {
        LoginResponse loginResponse = PBilibiliClient.Companion.getInstance().getBilibiliClient().getLoginResponse();
        if (loginResponse == null) {
            return;
        }
        BilibiliWebCookie bilibiliWebCookie = MyBilibiliClientUtil.getBilibiliWebCookie(loginResponse);
        try {
            String response = MyBilibiliClient.getInstance().getResponseByPost(new MyBilibiliClient.Request() {
                @Override
                public String getUrl() {
                    return "http://api.bilibili.com/x/click-interface/web/heartbeat";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("aid", String.valueOf(aid));
                    paramsMap.put("cid", String.valueOf(cid));
                    paramsMap.put("played_time", String.valueOf(playedTime));
                    paramsMap.put("csrf", bilibiliWebCookie.getBiliJct());
                }
            }, bilibiliWebCookie.getSessdata());
            Heartbeat historyReport = GsonUtil.getGsonInstance().fromJson(response, Heartbeat.class);
            if (historyReport.getCode() == 0) {
                callback.onSuccess(historyReport);
            } else {
                throw new BilibiliApiException(new CommonResponse(
                        historyReport.getCode(),
                        historyReport.getMessage(),
                        historyReport.getMessage(),
                        System.currentTimeMillis(),
                        null,
                        historyReport.getTtl()
                ));
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }

}
