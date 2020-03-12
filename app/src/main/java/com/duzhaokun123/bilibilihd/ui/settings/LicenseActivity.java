package com.duzhaokun123.bilibilihd.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.duzhaokun123.bilibilihd.R;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class LicenseActivity extends AppCompatActivity {

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
