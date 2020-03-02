package com.duzhaokun123.bilibilihd.ui.userSpace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.myBilibiliApi.space.SpaceAPI;
import com.duzhaokun123.bilibilihd.myBilibiliApi.space.model.Space;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.material.tabs.TabLayout;
//import com.hiczp.bilibili.api.app.model.Space;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.duzhaokun123.bilibilihd.utils.OtherUtils.setLevelDrawable;
import static com.duzhaokun123.bilibilihd.utils.OtherUtils.setSixDrawable;

public class UserSpaceActivity extends AppCompatActivity {

    private ImageView mIvSpaceImage, mIvSex,mIvLevel;
    private CircleImageView mCivFace;
    private TextView mTvName, mTvFans, mTvWatching, mTvLike, mTvSign;
    private TabLayout mTl;
    private ViewPager mVp;

    private Bundle bundle;
    private PBilibiliClient pBilibiliClient;
    private Space mSpace;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_space);

        mIvSpaceImage = findViewById(R.id.iv_spaceImage);
        mIvSex = findViewById(R.id.iv_sex);
        mIvLevel = findViewById(R.id.iv_level);
        mCivFace = findViewById(R.id.civ_face);
        mTvName = findViewById(R.id.tv_name);
        mTvFans = findViewById(R.id.tv_fans);
        mTvWatching = findViewById(R.id.tv_watching);
        mTvLike = findViewById(R.id.tv_like);
        mTvSign = findViewById(R.id.tv_sign);
        mTl = findViewById(R.id.tl);
        mVp = findViewById(R.id.vp);

        bundle = getIntent().getExtras();

        handler = new Handler();

        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try{
//                    mSpace = pBilibiliClient.getPAppAPI().space(bundle.getLong("uid"));
//                    handler.sendEmptyMessage(0);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Looper.prepare();
//                    ToastUtil.sendMsg(UserSpaceActivity.this, e.getMessage());
//                    Looper.loop();
//
//                }
                SpaceAPI spaceAPI = SpaceAPI.getSpaceAPI();
                spaceAPI.getSpace(bundle.getLong("uid"), new SpaceAPI.Callback() {
                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                        ToastUtil.sendMsg(UserSpaceActivity.this, e.getMessage());
                    }

                    @Override
                    public void onSuccess(com.duzhaokun123.bilibilihd.myBilibiliApi.space.model.Space space) {
                        mSpace = space;
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        }).start();

        mVp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        mTl.setupWithViewPager(mVp);

        mCivFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", mSpace.getData().getCard().getFace());
                startActivity(intent);
            }
        });
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private Fragment mHomeFragment, mTrendFragment, mSubmitFragment, mFavoriteFragment;

        public MyFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
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
                        mTrendFragment = new TrendFragment(mSpace);
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

            return null;
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

    class Handler extends android.os.Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            Glide.with(mCivFace).load(mSpace.getData().getCard().getFace()).into(mCivFace);
            if (mSpace.getData().getImages().getImgUrl().equals("")) {
                Glide.with(mIvSpaceImage).load("https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png").into(mIvSpaceImage);
                mIvSpaceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                        intent.putExtra("url", "https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png");
                        startActivity(intent);
                    }
                });
            } else {
                Glide.with(mIvSpaceImage).load(mSpace.getData().getImages().getImgUrl()).into(mIvSpaceImage);
                mIvSpaceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                        intent.putExtra("url", mSpace.getData().getImages().getImgUrl());
                        startActivity(intent);
                    }
                });
            }
            setSixDrawable(mIvSex, mSpace.getData().getCard().getSex());
            setLevelDrawable(mIvLevel, mSpace.getData().getCard().getLevelInfo().getCurrentLevel());
            mTvName.setText(mSpace.getData().getCard().getName());
            mTvFans.setText(String.valueOf(mSpace.getData().getCard().getFans()));
            mTvSign.setText(mSpace.getData().getCard().getSign());
            mTvWatching.setText(String.valueOf(mSpace.getData().getCard().getAttention()));
            if (mSpace.getData().getCard().getVip().getVipType() != 1) {
                mTvName.setTextColor(getColor(R.color.colorAccent));
            }
            // TODO: 20-2-22 试出 mTvLike 对应的键
        }
    }
}
