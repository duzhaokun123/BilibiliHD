package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.ui.MyBaseActivity;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class LicenseActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        getSupportFragmentManager().beginTransaction().add(R.id.fl,
                new LibsBuilder()
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "AboutLibraries")
                .supportFragment()
        ).commitAllowingStateLoss();
    }
}
