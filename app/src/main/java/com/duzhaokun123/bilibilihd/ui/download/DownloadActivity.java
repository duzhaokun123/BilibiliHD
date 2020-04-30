package com.duzhaokun123.bilibilihd.ui.download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityDownloadBinding;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;

public class DownloadActivity extends BaseActivity<ActivityDownloadBinding> {


    @Override
    protected int initConfig() {
        return FIX_LAYOUT;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_download;
    }

    @Override
    protected void initView() {
        baseBind.vp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        baseBind.tl.setupWithViewPager(baseBind.vp);
    }

    @Override
    protected void initData() {

    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private Fragment mDownloadingFragment, mDownloadedFragment;

        MyFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mDownloadingFragment == null) {
                        mDownloadingFragment = new DownloadingFragment();
                    }
                    return mDownloadingFragment;
                case 1:
                    if (mDownloadedFragment == null) {
                        mDownloadedFragment = new DownloadedFragment();
                    }
                    return mDownloadedFragment;
            }
            return new Fragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.downloading);
                case 1:
                    return getString(R.string.downloaded);
                default:
                    return super.getPageTitle(position);
            }
        }
    }
}
