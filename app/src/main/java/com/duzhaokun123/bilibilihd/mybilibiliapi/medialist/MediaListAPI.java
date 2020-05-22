package com.duzhaokun123.bilibilihd.mybilibiliapi.medialist;

import androidx.annotation.NonNull;

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model.Ids;
import com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model.Infos;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.hiczp.bilibili.api.retrofit.CommonResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import org.jetbrains.annotations.Contract;

import java.util.Map;

public class MediaListAPI {
    private static MediaListAPI mediaListAPI;

    public static MediaListAPI getInstance() {
        if (mediaListAPI == null) {
            mediaListAPI = new MediaListAPI();
        }
        return mediaListAPI;
    }

    private MediaListAPI() {
    }

    public void getInfos(long mediaId, long mid, @NonNull Ids ids, MyBilibiliClient.ICallback<Infos> callback) {
        Infos infos = null;

        for (int i = 0; i < ids.getData().size(); i += 20) {
            StringBuilder resourcesSB = new StringBuilder();
            for (int j = 0; j < 20 && i + j < ids.getData().size(); j++) {
                Ids.Data data = ids.getData().get(i + j);
                resourcesSB.append(data.getId()).append(':').append(data.getType()).append(',');
            }
            if (resourcesSB.length() > 0) {
                resourcesSB.deleteCharAt(resourcesSB.length() - 1);
            }
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
                if (infos == null) {
                    infos = GsonUtil.getGsonInstance().fromJson(response, Infos.class);
                } else {
                    Infos infos1 = GsonUtil.getGsonInstance().fromJson(response, Infos.class);
                    infos.getData().addAll(infos1.getData());
                    infos.setCode(infos1.getCode());
                    infos.setMessage(infos1.getMessage());
                }
                if (infos.getCode() != 0) {
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

        if (infos != null && infos.getCode() == 0) {
            callback.onSuccess(infos);
        }
    }

    public void getIds(long mediaId, long mid, MyBilibiliClient.ICallback<Ids> callback) {
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
