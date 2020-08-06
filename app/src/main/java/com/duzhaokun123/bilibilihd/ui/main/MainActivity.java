package com.duzhaokun123.bilibilihd.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityMainBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.JumpActivity;
import com.duzhaokun123.bilibilihd.ui.login.LoginActivity;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.ui.settings.SettingsActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.hiczp.bilibili.api.app.model.MyInfo;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private RelativeLayout mRlMyInfo;
    private TextView mTvUsername, mTvBBi, mTvCoins;
    private CircleImageView mCivFace;
    private ImageView mIvLevel;

    private Fragment homeFragment, historyFragment, dynamicFragment;

    private long lastBackPassTime = -1L;
    private PBilibiliClient pBilibiliClient = PBilibiliClient.Companion.getInstance();
    private MyInfo myInfo;
    private boolean first = true;
    private String title;
    private int defaultNavWidth = 0;

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.title = title.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onRestoreInstanceStateSynchronously(@NonNull Bundle savedInstanceState) {
        first = savedInstanceState.getBoolean("first");
        title = savedInstanceState.getString("title");
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadMyInfo();
    }

    @Override
    public void onBackPressed() {
        if (baseBind.dlMain != null && baseBind.dlMain.isOpen()) {
            baseBind.dlMain.close();
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (lastBackPassTime == -1L || currentTime - lastBackPassTime >= 2000) {
//            ToastUtil.sendMsg(MainActivity.this, R.string.passe_again_to_quit);
            TipUtil.showSnackbar(baseBind.flMain, R.string.passe_again_to_quit);
            lastBackPassTime = currentTime;
            return;
        }
        super.onBackPressed();
    }

    private void reloadMyInfo() {
        if (pBilibiliClient.getBilibiliClient().isLogin()) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        myInfo = pBilibiliClient.getPAppAPI().getMyInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            if (baseBind.dlMain == null) {
                                TipUtil.showTip(MainActivity.this, e.getMessage());
                            } else {
                                TipUtil.showToast(e.getMessage());
                            }
                        });
                    }

                    if (myInfo != null && handler != null) {
                        handler.sendEmptyMessage(0);
                    }
                }
            }.start();

        } else {
            if (handler != null) {
                handler.sendEmptyMessage(1);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (baseBind.dlMain != null) {
                    if (baseBind.dlMain.isOpen()) {
                        baseBind.dlMain.close();
                    } else {
                        baseBind.dlMain.open();
                    }
                }
                if (baseBind.ml != null) {
                    if (baseBind.ml.getProgress() == 0f) {
                        baseBind.ml.transitionToEnd();
                    } else {
                        baseBind.ml.transitionToStart();
                    }
                }
                return true;
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER | FIX_LAYOUT;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void findViews() {
        mRlMyInfo = (RelativeLayout) baseBind.navMain.getHeaderView(0);
        mTvUsername = mRlMyInfo.findViewById(R.id.tv_username);
        mIvLevel = mRlMyInfo.findViewById(R.id.iv_level);
        mTvBBi = mRlMyInfo.findViewById(R.id.tv_bBi);
        mTvCoins = mRlMyInfo.findViewById(R.id.tv_coins);
        mCivFace = mRlMyInfo.findViewById(R.id.civ_face);
    }

    @Override
    protected void initView() {
        if (homeFragment == null && first) {
            first = false;
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fl_main, homeFragment).commitAllowingStateLoss();
            setTitle(R.string.home);
        } else {
            setTitle(title);
        }

        if (baseBind.dlMain != null) {
            baseBind.dlMain.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    reloadMyInfo();
                }
            });
        }

        baseBind.navMain.setNavigationItemSelectedListener(item -> {
            if (baseBind.dlMain != null) {
                baseBind.dlMain.closeDrawers();
            }
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.home:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                    }
                    setTitle(R.string.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, homeFragment).commitAllowingStateLoss();
                    break;
                case R.id.dynamic:
                    if (dynamicFragment == null) {
                        dynamicFragment = new DynamicFragment();
                    }
                    setTitle(R.string.dynamic);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, dynamicFragment).commitAllowingStateLoss();
                    break;
                case R.id.history:
                    if (historyFragment == null) {
                        historyFragment = new HistoryFragment();
                    }
                    setTitle(R.string.history);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, historyFragment).commitAllowingStateLoss();
                    break;
                case R.id.settings:
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                    break;
                case R.id.junp:
                    intent = new Intent(MainActivity.this, JumpActivity.class);
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        });

        mRlMyInfo.setOnClickListener(v -> {
            if (pBilibiliClient.getBilibiliClient().isLogin()) {
                assert pBilibiliClient.getBilibiliClient().getUserId() != null;
                UserSpaceActivity.enter(this, pBilibiliClient.getBilibiliClient().getUserId(), mCivFace, mTvUsername);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Nullable
    @Override
    protected CoordinatorLayout initRegisterCoordinatorLayout() {
        return baseBind.flMain;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (defaultNavWidth == 0) {
            defaultNavWidth = baseBind.navMain.getWidth();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                Glide.with(MainActivity.this).load(myInfo.getData().getFace()).into(mCivFace);
                mTvUsername.setText(myInfo.getData().getName());
                ImageViewUtil.INSTANCE.setLevelDrawable(mIvLevel, myInfo.getData().getLevel());
//                    mTvBBi.setText(getString(R.string.b_bi) + ": " + myInfo.getData().get);
                mTvBBi.setText(getString(R.string.b_bi) + ": --");
                mTvCoins.setText(getString(R.string.coins) + ": " + myInfo.getData().getCoins());
                if (myInfo.getData().getVip().getType() != 0) {
                    mTvUsername.setTextColor(getColor(R.color.colorAccent));
                }
                break;
            case 1:
                mCivFace.setImageDrawable(null);
                mTvUsername.setText(R.string.not_logged_in);
                mIvLevel.setImageDrawable(null);
                mTvBBi.setText(getString(R.string.b_bi) + ": --");
                mTvCoins.setText(getString(R.string.coins) + ": --");
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("first", first);
        outState.putString("title", title);
    }
}
