package com.duzhaokun123.bilibilihd.mybilibiliapi.medialist;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model.Ids;
import com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model.Infos;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.util.Map;

public class MediaListAPI {
    private static MediaListAPI mediaListAPI;

    public static MediaListAPI getInstance() {
        if (mediaListAPI == null) {
            mediaListAPI = new MediaListAPI();
        }
        return mediaListAPI;
    }

    private MediaListAPI() {}

    private PBilibiliClient pBilibiliClient;

    public void getInfos(long mediaId, long mid,Ids ids, MyBilibiliClient.ICallback<Infos> callback) {
        if (pBilibiliClient == null) {
            pBilibiliClient = PBilibiliClient.Companion.getInstance();
        }
        StringBuilder resourcesSB = new StringBuilder();
        for (Ids.Data data : ids.getData()) {
            resourcesSB.append(data.getId()).append(':').append(data.getType()).append(',');
        }
        resourcesSB.deleteCharAt(resourcesSB.length() - 1);
        try {
            String response = MyBilibiliClient.getInstance().getResponseByGet(new MyBilibiliClient.GetRequest() {
                @Override
                public String getUrl() {
                    return "https://api.bilibili.com/medialist/gateway/base/resource/infos";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("media_id", String.valueOf(mediaId));
                    paramsMap.put("mobi_app", "android");
                    paramsMap.put("resources", resourcesSB.toString());
                    paramsMap.put("mid", String.valueOf(mid));
                }
            });
            Infos infos = GsonUtil.getGsonInstance().fromJson(response, Infos.class);
            if (infos.getCode() == 0) {
                callback.onSuccess(infos);
            } else {
                throw new BilibiliApiException(new CommonResponse(
                        infos.getCode(),
                        infos.getMessage(),
                        infos.getMessage(),
                        System.currentTimeMillis(),
                        null,
                        0 //没有ttl
                ));
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }

    public void getIds(long mediaId, long mid, MyBilibiliClient.ICallback<Ids> callback) {
        if (pBilibiliClient == null) {
            pBilibiliClient = PBilibiliClient.Companion.getInstance();
        }
        try {
            String response = MyBilibiliClient.getInstance().getResponseByGet(new MyBilibiliClient.GetRequest() {
                @Override
                public String getUrl() {
                    return "https://api.bilibili.com/medialist/gateway/base/resource/ids";
                }

                @Override
                public void addUserParams(Map<String, String> paramsMap) {
                    paramsMap.put("media_id", String.valueOf(mediaId));
                    paramsMap.put("mobi_app", "android");
                    paramsMap.put("mid", String.valueOf(mid));
                }
            });
            Ids ids = GsonUtil.getGsonInstance().fromJson(response, Ids.class);
            if (ids.getCode() == 0) {
                callback.onSuccess(ids);
            } else {
                throw new BilibiliApiException(new CommonResponse(
                        ids.getCode(),
                        ids.getMessage(),
                        ids.getMessage(),
                        System.currentTimeMillis(),
                        null,
                        0 //没有ttl
                ));
            }
        } catch (Exception e) {
            callback.onException(e);
        }
    }
}
