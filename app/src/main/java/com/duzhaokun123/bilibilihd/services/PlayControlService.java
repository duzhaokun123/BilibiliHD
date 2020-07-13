package com.duzhaokun123.bilibilihd.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class PlayControlService extends IntentService {
    public static final String ACTION_PAUSE = "com.duzhaokun123.bilibilihd.services.action.PAUSE";
    public static final String ACTION_RESUME = "com.duzhaokun123.bilibilihd.services.action.RESUME";

    public static final String EXTRA_ID = "com.duzhaokun123.bilibilihd.services.extra.ID";

    public PlayControlService() {
        super("PlayControlService");
    }

    private static Map<Long, ICallback> idICallbackMap;

    private static Map<Long, ICallback> getIdICallbackMap() {
        if (idICallbackMap == null) {
            idICallbackMap = new HashMap<>();
        }
        return idICallbackMap;
    }

    public static void putId(long id, ICallback callback) {
        getIdICallbackMap().put(id, callback);
    }

    public static void removeId(long id) {
        getIdICallbackMap().remove(id);
    }

    public static PendingIntent newPausePendingIntent(Context context, long id) {
        Intent pauseIntent = new Intent(context, PlayControlService.class);
        pauseIntent.setAction(PlayControlService.ACTION_PAUSE);
        pauseIntent.putExtra(PlayControlService.EXTRA_ID, id);
        return PendingIntent.getService(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent newResumePendingIntent(Context context, long id) {
        Intent pauseIntent = new Intent(context, PlayControlService.class);
        pauseIntent.setAction(PlayControlService.ACTION_RESUME);
        pauseIntent.putExtra(PlayControlService.EXTRA_ID, id);
        return PendingIntent.getService(context, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PAUSE.equals(action)) {
                final long id = intent.getLongExtra(EXTRA_ID, 0);
                handleActionPause(id);
            } else if (ACTION_RESUME.equals(action)) {
                final long id = intent.getLongExtra(EXTRA_ID, 0);
                handleActionResume(id);
            }
        }
    }

    private void handleActionPause(long id) {
        Log.d("PlayControlService", "here");
        ICallback callback = getIdICallbackMap().get(id);
        if (callback != null) {
            callback.onPause();
        }
    }

    private void handleActionResume(long id) {
        ICallback callback = getIdICallbackMap().get(id);
        if (callback != null) {
            callback.onResume();
        }
    }

    public interface ICallback {
        void onPause();

        void onResume();
    }
}
