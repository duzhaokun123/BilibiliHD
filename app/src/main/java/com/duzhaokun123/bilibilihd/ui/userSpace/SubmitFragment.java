package com.duzhaokun123.bilibilihd.ui.userSpace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.R;

//import com.hiczp.bilibili.api.app.model.Space;
import com.duzhaokun123.bilibilihd.myBilibiliApi.space.model.Space;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class SubmitFragment extends Fragment {

    private XRecyclerView mXrv;

    private Space space;

    public SubmitFragment(){}

    public SubmitFragment(Space space) {
        this.space = space;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xrecyclerview_only, container, false);
        mXrv = view.findViewById(R.id.xrv);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
