package com.duzhaokun123.bilibilihd.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.BuildConfig;
import com.duzhaokun123.bilibilihd.R;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView mTvVersion = view.findViewById(R.id.version);
        TextView mTvBuildType = view.findViewById(R.id.build_type);
        Button mBtnLicense = view.findViewById(R.id.btn_license);
        Button mBtnProjectHome = view.findViewById(R.id.btn_projectHome);

        mTvVersion.setText(getString(R.string.version) + ": " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        mTvBuildType.setText(getString(R.string.build_type) + ": " + BuildConfig.BUILD_TYPE);
        mBtnLicense.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LicenseActivity.class);
            startActivity(intent);
        });
        mBtnProjectHome.setOnClickListener(v -> {
                Uri uri = Uri.parse(BuildConfig.PROJECT_HOME_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
        });
    }
}
