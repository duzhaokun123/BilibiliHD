package com.duzhaokun123.bilibilihd.ui.userSpace;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.R;
import com.hiczp.bilibili.api.app.model.Space;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class TrendFragment extends Fragment {

    private XRecyclerView mXrv;
    private Context mContext;

    private Space space;

    public TrendFragment(){}

    public TrendFragment(Context context, Space space) {
        this.space = space;
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_only_xrecyclerview, container, false);
        mXrv = view.findViewById(R.id.xrv);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
