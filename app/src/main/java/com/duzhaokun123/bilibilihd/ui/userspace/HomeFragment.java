package com.duzhaokun123.bilibilihd.ui.userspace;

import android.graphics.Color;
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
import com.hiczp.bilibili.api.app.model.Space;

public class HomeFragment extends Fragment {

    private RecyclerView mRv;

    private Space space;

    public HomeFragment(){}

    public HomeFragment(Space space) {
        this.space = space;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recyclerview_with_v, container, false);
        mRv = view.findViewById(R.id.rv);
        view.setBackgroundColor(Color.BLUE);
        return view;
    }

    // TODO: 20-2-23 主页比较复杂以后做
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRv.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.column_medium)));
        mRv.setAdapter(new RecyclerView.Adapter() {
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
