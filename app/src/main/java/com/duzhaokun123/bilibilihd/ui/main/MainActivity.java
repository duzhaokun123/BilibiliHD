package com.duzhaokun123.bilibilihd.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.JumpActivity;
import com.duzhaokun123.bilibilihd.ui.LoginActivity;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.ui.settings.SettingsActivity;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.material.navigation.NavigationView;
import com.hiczp.bilibili.api.app.model.MyInfo;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.duzhaokun123.bilibilihd.utils.OtherUtils.setLevelDrawable;

public class MainActivity extends AppCompatActivity {

    private CircleImageView mCivFace;
    private DrawerLayout mDlMain;
    private TextView mTvUsername, mTvBBi, mTvCoins;
    private NavigationView mNavMain;
    private RelativeLayout mRlMyInfo;
    private ImageView mIvLevel;
    private Fragment homeFragment, historyFragment, dynamicFragment;

    private long lastBackPassTime = -1L;
    private PBilibiliClient pBilibiliClient;
    private Handler handler;
    private MyInfo myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        try {// FIXME: 20-2-21 这里一定有更优解
            mDlMain = findViewById(R.id.dl_main);
        } catch (IllegalArgumentException e) {

        }
        mNavMain = findViewById(R.id.nav_main);
        mRlMyInfo = (RelativeLayout) mNavMain.getHeaderView(0);
        mTvUsername = mRlMyInfo.findViewById(R.id.tv_username);
        mIvLevel = mRlMyInfo.findViewById(R.id.iv_level);
        mTvBBi = mRlMyInfo.findViewById(R.id.tv_bBi);
        mTvCoins = mRlMyInfo.findViewById(R.id.tv_coins);
        mCivFace = mRlMyInfo.findViewById(R.id.civ_face);

        setTitle(R.string.home);
        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        handler = new Handler();

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fl_main, homeFragment).commitAllowingStateLoss();

        if (mDlMain != null) {
            mDlMain.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    reloadMyInfo();
                }
            });
        }

        mNavMain.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    mDlMain.closeDrawers();
                } catch (NullPointerException e) {

                }
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.home:
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
                        startActivity(intent);
                        break;
                    case R.id.junp:
                        intent = new Intent(MainActivity.this, JumpActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        mRlMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pBilibiliClient.getBilibiliClient().isLogin()) {
                    Intent intent = new Intent(MainActivity.this, UserSpaceActivity.class);
                    intent.putExtra("uid", pBilibiliClient.getBilibiliClient().getUserId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadMyInfo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long currentTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (lastBackPassTime == -1L || currentTime - lastBackPassTime >= 2000) {
                ToastUtil.sendMsg(MainActivity.this, R.string.passe_again_to_quit);
                lastBackPassTime = currentTime;
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
                        Looper.prepare();
                        ToastUtil.sendMsg(MainActivity.this, e.getMessage());
                        Looper.loop();
                    }

                    if (myInfo != null) {
                        handler.sendEmptyMessage(0);
                    }
                }
            }.start();

        } else {
            handler.sendEmptyMessage(1);
        }
    }

    class Handler extends android.os.Handler {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Glide.with(MainActivity.this).load(myInfo.getData().getFace()).into(mCivFace);
                    mTvUsername.setText(myInfo.getData().getName());
                    setLevelDrawable(mIvLevel, myInfo.getData().getLevel());
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDlMain != null) {
                    if (mDlMain.isOpen()) {
                        mDlMain.close();
                    } else {
                        mDlMain.open();
                    }
                } else {
                    // TODO: 20-3-10  
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
