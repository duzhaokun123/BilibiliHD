package com.duzhaokun123.bilibilihd.utils;

import androidx.annotation.Nullable;

import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.io.Serializable;
import java.util.HashMap;

public class LoginUserInfoMap extends HashMap<Long, LoginResponse> implements Serializable {

    private long loggedUid;

    public long getLoggedUdi() {
        return loggedUid;
    }

    public void setLoggedUid(long loggedUid) {
        this.loggedUid = loggedUid;
    }

    @Nullable
    public LoginResponse getLoggedLoginResponse() {
        if (loggedUid != 0) {
            return get(loggedUid);
        } else {
            return null;
        }
    }

    public LoginResponse getByIndex(int index) {
        return get(keySet().toArray()[index]);
    }

    @Nullable
    @Override
    public LoginResponse remove(@Nullable Object key) {
        LoginResponse loginResponse = get(key);
        if (loginResponse != null && loginResponse.getUserId() == getLoggedUdi()) {
            loggedUid = 0;
        }
        return super.remove(key);
    }
}
