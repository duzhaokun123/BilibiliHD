package com.duzhaokun123.bilibilihd.ui.userSpace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzhaokun123.bilibilihd.R;
//import com.hiczp.bilibili.api.app.model.Space;
import com.duzhaokun123.bilibilihd.myBilibiliApi.space.model.Space;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class HomeFragment extends Fragment {

    private XRecyclerView mXrv;

    private Space space;

    public HomeFragment(){}

    public HomeFragment(Space space) {
        this.space = space;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xrecyclerview_only, container, false);
        mXrv = view.findViewById(R.id.xrv);
        return view;
    }

    // TODO: 20-2-23 主页比较复杂以后做
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mXrv.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.column_medium)));
        mXrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
    }
}
