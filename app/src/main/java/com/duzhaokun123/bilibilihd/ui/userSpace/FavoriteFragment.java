package com.duzhaokun123.bilibilihd.ui.userSpace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzhaokun123.bilibilihd.R;
import com.hiczp.bilibili.api.app.model.Space;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class FavoriteFragment extends Fragment {

    private XRecyclerView mXrv;
    private Context mContext;

    private Space space;

    public FavoriteFragment(){}

    public FavoriteFragment(Context context, Space space) {
        this.space = space;
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_only_xrecyclerview, container, false);
        mXrv = view.findViewById(R.id.xrv);
        mXrv.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.column_medium)));
        mXrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FavoriteCardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_favorite_card_item, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((FavoriteCardViewHolder)holder).mTvName.setText(space.getData().getFavourite().getItem().get(position).getName());
                ((FavoriteCardViewHolder)holder).mTvCount.setText(space.getData().getFavourite().getItem().get(position).getCurCount() + getString(R.string.content));
            }

            @Override
            public int getItemCount() {
                return space.getData().getFavourite().getItem().size();
            }

            class FavoriteCardViewHolder extends RecyclerView.ViewHolder {

                private ImageView mIv;
                private TextView mTvName, mTvCount;

                public FavoriteCardViewHolder(@NonNull View itemView) {
                    super(itemView);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvName = itemView.findViewById(R.id.tv_name);
                    mTvCount = itemView.findViewById(R.id.tv_count);
                }
            }
        });
        mXrv.setLoadingMoreEnabled(false);
        mXrv.setPullRefreshEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
