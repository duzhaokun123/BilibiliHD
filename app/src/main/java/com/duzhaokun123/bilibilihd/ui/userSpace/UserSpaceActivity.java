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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.material.tabs.TabLayout;
import com.hiczp.bilibili.api.app.model.Space;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.duzhaokun123.bilibilihd.utils.OtherUtils.setLevelDrawable;
import static com.duzhaokun123.bilibilihd.utils.OtherUtils.setSixDrawable;

public class UserSpaceActivity extends AppCompatActivity {

    private ImageView mIvSpeaceImage, mIvSex,mIvLevel;
    private CircleImageView mCivFace;
    private TextView mTvName, mTvFans, mTvWatching, mTvLike, mTvSign;
    private TabLayout mTl;
    private ViewPager mVp;

    private Bundle bundle;
    private PBilibiliClient pBilibiliClient;
    private Space space;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_space);

        mIvSpeaceImage = findViewById(R.id.iv_spaceImage);
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

        // FIXME: 20-2-22 你看下面黄的
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Glide.with(mCivFace).load(space.getData().getCard().getFace()).into(mCivFace);
                setSixDrawable(mIvSex, space.getData().getCard().getSex());
                setLevelDrawable(mIvLevel, space.getData().getCard().getLevelInfo().getCurrentLevel());
                mTvName.setText(space.getData().getCard().getName());
                mTvFans.setText(String.valueOf(space.getData().getCard().getFans()));
                mTvSign.setText(space.getData().getCard().getSign());
                mTvWatching.setText(String.valueOf(space.getData().getCard().getAttention()));
                // TODO: 20-2-22 试出 mTvLike 和 mIvSpaceImage 对应的键
            }
        };

        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    space = pBilibiliClient.getPAppAPI().space(bundle.getLong("uid"));
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    ToastUtil.sendMsg(UserSpaceActivity.this, e.getMessage());
                    Looper.loop();

                }
            }
        }).start();

        mVp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        mTl.setupWithViewPager(mVp);

        mCivFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSpaceActivity.this, PhotoViewActivity.class);
                intent.putExtra("url", space.getData().getCard().getFace());
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
                        mHomeFragment = new HomeFragment(space);
                    }
                    return mHomeFragment;
                case 1:
                    if (mTrendFragment == null) {
                        mTrendFragment = new TrendFragment(space);
                    }
                    return mTrendFragment;
                case 2:
                    if (mSubmitFragment == null) {
                        mSubmitFragment = new SubmitFragment(space);
                    }
                    return mSubmitFragment;
                case 3:
                    if (mFavoriteFragment == null) {
                        mFavoriteFragment = new FavoriteFragment(space);
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
}
