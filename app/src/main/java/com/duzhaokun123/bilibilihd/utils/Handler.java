package com.duzhaokun123.bilibilihd.utils;

import android.os.Message;

import androidx.annotation.NonNull;

public class Handler extends android.os.Handler {

    private IHandlerMessageCallback iHandlerMessageCallback;

    public Handler(IHandlerMessageCallback iHandlerMessageCallback) {
        this.iHandlerMessageCallback = iHandlerMessageCallback;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (iHandlerMessageCallback != null) {
            iHandlerMessageCallback.handlerCallback(msg);
        }
    }

    public void destroy() {
        removeCallbacksAndMessages(null);
        iHandlerMessageCallback = null;
    }

    public interface IHandlerMessageCallback {
        default void  handlerCallback(@NonNull Message msg) { }
    }
}
