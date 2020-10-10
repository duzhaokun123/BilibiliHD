package com.duzhaokun123.bilibilihd.ui.userspace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityUserSpaceBinding;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.utils.BilibiliUrlUtil;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.LinkifyUtil;
import com.duzhaokun123.bilibilihd.utils.ObjectCache;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hiczp.bilibili.api.app.model.Space;

import java.util.ArrayList;

public class UserSpaceActivity extends BaseActivity<ActivityUserSpaceBinding> {
    public static final String EXTRA_UID = "uid";
    public static final String EXTRA_FACE_CACHE_ID = "face_cache_id";
    public static final String EXTRA_NAME_CACHE_ID = "name_cache_id";

    @SuppressWarnings("unchecked")
    public static void enter(Activity activity, long uid,
                             @Nullable View faceView, @Nullable Drawable faceDrawable,
                             @Nullable View nameView, @Nullable CharSequence nameCS) {
        Intent intent = new Intent(activity, UserSpaceActivity.class);
        intent.putExtra(EXTRA_UID, uid);
        if (Settings.layout.isUserSpaceUseWebView()) {
            BrowserUtil.openWebViewActivity(activity, "https://space.bilibili.com/" + uid, true, true);
        } else {
            intent.putExtra(EXTRA_FACE_CACHE_ID, ObjectCache.put(faceDrawable));
            intent.putExtra(EXTRA_NAME_CACHE_ID, ObjectCache.put(nameCS));
            ArrayList<Pair<View, String>> pairs = new ArrayList<>();
            if (faceView != null) {
                pairs.add(Pair.create(faceView, "face"));
            }
            if (nameView != null) {
                pairs.add(Pair.create(nameView, "name"));
            }
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity, pairs.toArray(new Pair[0])).toBundle());
        }
    }

    public static void enter(Activity activity, long uid, @Nullable ImageView faceView, @Nullable TextView nameView) {
        enter(activity, uid, faceView, faceView != null ? faceView.getDrawable() : null, nameView, nameView != null ? nameView.getText() : null);
    }

    private Space mSpace;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_space_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.open_in_browser) {
            BrowserUtil.openCustomTab(this, BilibiliUrlUtil.getUserSpaceLink(getStartIntent().getLongExtra(EXTRA_UID, 0)));
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
        if (Settings.layout.isUserSpaceUseWebView()) {
            BrowserUtil.openWebViewActivity(this, "https://space.bilibili.com/" + getStartIntent().getLongExtra(EXTRA_UID, 0), true, true);
            finish();
            return;
        }

        baseBind.civFace.setImageDrawable((Drawable) ObjectCache.get(getStartIntent().getStringExtra(EXTRA_FACE_CACHE_ID)));
        baseBind.tvName.setText((CharSequence) ObjectCache.get(getStartIntent().getStringExtra(EXTRA_NAME_CACHE_ID)));

        baseBind.vp.setAdapter(new MyFragmentStateAdapter(this));
        new TabLayoutMediator(baseBind.tl, baseBind.vp, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.home);
                case 1:
                    tab.setText(R.string.dynamic);
                case 2:
                    tab.setText(R.string.submit);
                case 3:
                    tab.setText(R.string.favorite);
            }
        }).attach();

        baseBind.civFace.setOnClickListener(v -> {
            if (mSpace != null) {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", mSpace.getData().getCard().getFace());
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, baseBind.civFace, "img").toBundle());
            }
        });

        setTitle(null);


        baseBind.tvFans.setOnClickListener(v -> BrowserUtil.openWebViewActivity(this,
                "https://space.bilibili.com/h5/follow?type=fans&mid" + getStartIntent().getLongExtra(EXTRA_UID, 0) + "&night=" + (OtherUtils.isNightMode() ? 1 : 0), false, true));
        baseBind.tvWatching.setOnClickListener(v -> BrowserUtil.openWebViewActivity(this,
                "https://space.bilibili.com/h5/follow?type=follow&mid" + getStartIntent().getLongExtra(EXTRA_UID, 0) + "night=" + (OtherUtils.isNightMode() ? 1 : 0), false, true));

    }

    @Override
    public void initData() {
        if (Settings.layout.isUserSpaceUseWebView()) {
            return;
        }
//        new Thread() {
//            @Override
//            public void run() {
//
//                SpaceAPI.getInstance().getSpace(, new MyBilibiliClient.ICallback<Space>() {
//                    @Override
//                    public void onException(@NotNull @NonNull Exception e) {
//                        e.printStackTrace();
//
//                    }
//
//                    @Override
//                    public void onSuccess(@NotNull @NonNull Space space) {
//                        mSpace = space;
//                        if (handler != null) {
//
//                        }
//                    }
//                });
//
//            }
//        }.start();
        new Thread(() -> {
            try {
                mSpace = Application.getPBilibiliClient().getPAppAPI().space(getStartIntent().getLongExtra(EXTRA_UID, 0));
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            }catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> TipUtil.showToast(e.getMessage()));
            }
        }).start();
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        Glide.with(this).load(mSpace.getData().getCard().getFace()).into(baseBind.civFace);
        if (mSpace.getData().getImages().getImgUrl().equals("")) {
            Glide.with(this).load("https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png").into(baseBind.ivSpaceImage);
            baseBind.ivSpaceImage.setOnClickListener(v -> {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", "https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, baseBind.ivSpaceImage, "img").toBundle());
            });
        } else {
            Glide.with(this).load(mSpace.getData().getImages().getImgUrl()).into(baseBind.ivSpaceImage);
            baseBind.ivSpaceImage.setOnClickListener(v -> {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", mSpace.getData().getImages().getImgUrl());
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, baseBind.ivSpaceImage, "img").toBundle());
            });
        }
        ImageViewUtil.INSTANCE.setSixDrawable(baseBind.ivSex, mSpace.getData().getCard().getSex());
        ImageViewUtil.INSTANCE.setLevelDrawable(baseBind.ivLevel, mSpace.getData().getCard().getLevelInfo().getCurrentLevel());
        baseBind.tvName.setText(mSpace.getData().getCard().getName());
        baseBind.tvFans.setText(String.valueOf(mSpace.getData().getCard().getFans()));
        baseBind.tvSign.setText(mSpace.getData().getCard().getSign());
        LinkifyUtil.INSTANCE.addAllLinks(baseBind.tvSign);
        baseBind.tvWatching.setText(String.valueOf(mSpace.getData().getCard().getAttention()));
        if (mSpace.getData().getCard().getVip().getVipType() != 0) {
            baseBind.tvName.setTextColor(getColor(R.color.colorAccent));
        }
    }

    class MyFragmentStateAdapter extends FragmentStateAdapter {

        private Fragment mHomeFragment, mTrendFragment, mSubmitFragment, mFavoriteFragment;

        public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
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
        public int getItemCount() {
            return 4;
        }
    }
}
