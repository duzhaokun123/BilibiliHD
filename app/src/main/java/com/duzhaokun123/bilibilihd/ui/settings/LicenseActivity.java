package com.duzhaokun123.bilibilihd.ui.settings;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityLicenseBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class LicenseActivity extends BaseActivity<ActivityLicenseBinding> {

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_license;
    }

    @Override
    public void initView() {
        getSupportFragmentManager().beginTransaction().add(R.id.fl,
                new LibsBuilder()
                        .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "AboutLibraries")
                        .supportFragment()
        ).commitAllowingStateLoss();
    }

    @Override
    public void initData() {

    }
}
