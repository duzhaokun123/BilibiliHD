package com.duzhaokun123.bilibilihd.ui.userspace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityUserSpaceBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.SpaceAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.utils.BilibiliUrlUtil;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;

public class UserSpaceActivity extends BaseActivity<ActivityUserSpaceBinding> {
    private Space mSpace;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.user_space_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.open_in_browser && teleportIntent != null && teleportIntent.getExtras() != null) {
            BrowserUtil.openCustomTab(this, BilibiliUrlUtil.getUserSpaceLink(teleportIntent.getExtras().getLong("uid")));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_user_space;
    }

    @Override
    public void initView() {
        baseBind.tl.setupWithViewPager(baseBind.vp);

        baseBind.civFace.setOnClickListener(v -> {
            if (mSpace != null) {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", mSpace.getData().getCard().getFace());
                startActivity(intent);
            }
        });

        setTitle(null);
    }

    @Override
    public void initData() {
        new Thread() {
            @Override
            public void run() {
                if (teleportIntent != null && teleportIntent.getExtras() != null) {
                    SpaceAPI.getInstance().getSpace(teleportIntent.getExtras().getLong("uid"), new MyBilibiliClient.ICallback<Space>() {
                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> TipUtil.showToast(e.getMessage()));
                        }

                        @Override
                        public void onSuccess(com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space space) {
                            mSpace = space;
                            if (handler != null) {
                                handler.sendEmptyMessage(0);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        baseBind.vp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        Glide.with(this).load(mSpace.getData().getCard().getFace()).into(baseBind.civFace);
        if (mSpace.getData().getImages().getImgUrl().equals("")) {
            Glide.with(this).load("https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png").into(baseBind.ivSpaceImage);
            baseBind.ivSpaceImage.setOnClickListener(v -> {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", "https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png");
                startActivity(intent);
            });
        } else {
            Glide.with(this).load(mSpace.getData().getImages().getImgUrl()).into(baseBind.ivSpaceImage);
            baseBind.ivSpaceImage.setOnClickListener(v -> {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", mSpace.getData().getImages().getImgUrl());
                startActivity(intent);
            });
        }
        ImageViewUtil.setSixDrawable(baseBind.ivSex, mSpace.getData().getCard().getSex());
        ImageViewUtil.setLevelDrawable(baseBind.ivLevel, mSpace.getData().getCard().getLevelInfo().getCurrentLevel());
        baseBind.tvName.setText(mSpace.getData().getCard().getName());
        baseBind.tvFans.setText(String.valueOf(mSpace.getData().getCard().getFans()));
        baseBind.tvSign.setText(mSpace.getData().getCard().getSign());
        baseBind.tvWatching.setText(String.valueOf(mSpace.getData().getCard().getAttention()));
        if (mSpace.getData().getCard().getVip().getVipType() != 0) {
            baseBind.tvName.setTextColor(getColor(R.color.colorAccent));
        }
        // TODO: 20-2-22 试出 mTvLike 对应的键 不可能的, 你的版本不行, 去抓 web api 吧
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {


        private Fragment mHomeFragment, mTrendFragment, mSubmitFragment, mFavoriteFragment;

        MyFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mHomeFragment == null) {
                        mHomeFragment = new HomeFragment(mSpace);
                    }
                    return mHomeFragment;
                case 1:
                    if (mTrendFragment == null) {
                        mTrendFragment = new DynamicFragment(mSpace);
                    }
                    return mTrendFragment;
                case 2:
                    if (mSubmitFragment == null) {
                        mSubmitFragment = new SubmitFragment(mSpace);
                    }
                    return mSubmitFragment;
                case 3:
                    if (mFavoriteFragment == null) {
                        mFavoriteFragment = new FavoriteFragment(mSpace);
                    }
                    return mFavoriteFragment;
            }

            return new Fragment();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.home);
                case 1:
                    return getString(R.string.dynamic);
                case 2:
                    return getString(R.string.submit);
                case 3:
                    return getString(R.string.favorite);
            }
            return super.getPageTitle(position);
        }

    }
}
