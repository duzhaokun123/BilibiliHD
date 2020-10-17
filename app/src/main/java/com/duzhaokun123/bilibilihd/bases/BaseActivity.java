package com.duzhaokun123.bilibilihd.bases;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DisplayCutout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.duzhaokun123.bilibilihd.utils.Handler;
import com.duzhaokun123.bilibilihd.utils.TipUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity<layout extends ViewDataBinding> extends AppCompatActivity implements Handler.IHandlerMessageCallback {

    protected static final int FULLSCREEN = 0b001;
    protected static final int NEED_HANDLER = 0b010;
    protected static final int FIX_LAYOUT = 0b0100;
    protected static final int DISABLE_FULLSCREEN_LAYOUT = 0b01000;

    private int config;
    private boolean layoutFixed = false;

    public final String CLASS_NAME = this.getClass().getSimpleName();
    protected layout baseBind;
    @Nullable
    protected Handler handler;
    /**
     * actionBarHeight displayCutout 只在 onWindowFocusChanged() 后才有意义
     */
    public int stateBarHeight, actionBarHeight, navigationBarHeight;
    public boolean navigationBarOnButton;
    @Nullable
    public DisplayCutout displayCutout;

    private Map<Integer, IRequestPermissionCallback> iRequestPermissionCallbackMap;
    private int permissionNum = 0;
    private boolean firstCreate = true;

    /**
     * 只在上部刘海屏与 stateBar 同高时正确
     */
    public int getFixTopHeight() {
        if (displayCutout != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && displayCutout.getSafeInsetTop() != 0) {
            return actionBarHeight + displayCutout.getSafeInsetTop();
        } else {
            return actionBarHeight + stateBarHeight;
        }
    }

    public int getFixButtonHeight() {
        if (!navigationBarOnButton) {
            return 0;
        } else if (displayCutout != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && displayCutout.getSafeInsetBottom() != 0) {
            return navigationBarHeight + displayCutout.getSafeInsetBottom();
        } else {
            return navigationBarHeight;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            firstCreate = false;
        }

        super.onCreate(savedInstanceState);

        config = initConfig();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), (config & DISABLE_FULLSCREEN_LAYOUT) != 0);

        if ((config & FULLSCREEN) != 0) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if ((config & NEED_HANDLER) != 0) {
            handler = new Handler(this);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        baseBind = DataBindingUtil.setContentView(this, initLayout());

        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            stateBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        if (savedInstanceState != null) {
            onRestoreInstanceStateSynchronously(savedInstanceState);
        }

        findViews();
        initView();
        initData();
        TipUtil.registerCoordinatorLayout(this, initRegisterCoordinatorLayout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((config & FULLSCREEN) != 0) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TipUtil.unregisterCoordinatorLayout(this);
        if (handler != null) {
            handler.destroy();
            handler = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int viewHeight = displayMetrics.heightPixels;
        int decorViewHeight = getWindow().getDecorView().getHeight();
        navigationBarOnButton = decorViewHeight != viewHeight;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            displayCutout = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
        }

        if ((config & DISABLE_FULLSCREEN_LAYOUT) == 0 && (config & FIX_LAYOUT) != 0 && !layoutFixed) {
            layoutFixed = true;

            actionBarHeight = -1;
            if (getSupportActionBar() != null) {
                actionBarHeight = getSupportActionBar().getHeight();
            }
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) baseBind.getRoot().getLayoutParams();
            if (navigationBarOnButton) {
                params.topMargin = getFixTopHeight();
            } else {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                        & ~(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE));
                params.topMargin = actionBarHeight / 2; // fixme: 不知道为什么, 就是要这么算
            }
            baseBind.getRoot().setLayoutParams(params);

            onLayoutFixInfoReady();
        }

        if ((config & DISABLE_FULLSCREEN_LAYOUT) == 0 && (config & FIX_LAYOUT) != 0 && layoutFixed && !navigationBarOnButton) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                    & ~(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE));
        } else if ((config & DISABLE_FULLSCREEN_LAYOUT) != 0) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                    & ~(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE));
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("permissionNum", permissionNum);
    }

    public void requestPermissions(String[] permissions, @Nullable IRequestPermissionCallback iRequestPermissionCallback) {
        if (iRequestPermissionCallbackMap == null) {
            iRequestPermissionCallbackMap = new HashMap<>();
        }
        iRequestPermissionCallbackMap.put(permissionNum, iRequestPermissionCallback);
        requestPermissions(permissions, permissionNum);
        permissionNum++;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (iRequestPermissionCallbackMap != null) {
            IRequestPermissionCallback iRequestPermissionCallback = iRequestPermissionCallbackMap.get(requestCode);
            if (iRequestPermissionCallback != null) {
                iRequestPermissionCallback.callback(grantResults);
            }
            iRequestPermissionCallbackMap.remove(requestCode);
            if (iRequestPermissionCallbackMap.size() == 0) {
                iRequestPermissionCallbackMap = null;
            }
        }
    }

    @Nullable
    public Handler getHandler() {
        return handler;
    }

    public boolean isFirstCreate() {
        return firstCreate;
    }

    protected abstract int initConfig();

    protected abstract int initLayout();

    protected void onRestoreInstanceStateSynchronously(@NonNull Bundle savedInstanceState) {
        permissionNum = savedInstanceState.getInt("permissionNum");
    }

    protected void findViews() {
    }

    protected abstract void initView();

    protected abstract void initData();

    public interface IRequestPermissionCallback {
        void callback(int[] grantResults);
    }

    protected void onLayoutFixInfoReady() {
    }

    @NonNull
    public Intent getStartIntent() {
        return getIntent();
    }

    @Nullable
    protected CoordinatorLayout initRegisterCoordinatorLayout() {
        return null;
    }
}
