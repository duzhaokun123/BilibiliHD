package com.duzhaokun123.bilibilihd.ui.userspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
//import com.hiczp.bilibili.api.app.model.Space;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class FavoriteFragment extends Fragment {

    private XRecyclerView mXrv;

    private Space space;

    public FavoriteFragment(){}

    public FavoriteFragment(Space space) {
        this.space = space;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            space = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("space"), Space.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_xrecyclerview_only, container, false);
        mXrv = view.findViewById(R.id.xrv);
        mXrv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mXrv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
            }
        });
        mXrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FavoriteCardViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_favorite_card_item, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((FavoriteCardViewHolder)holder).mTvName.setText(space.getData().getFavourite().getItem().get(position).getName());
                ((FavoriteCardViewHolder)holder).mTvCount.setText(space.getData().getFavourite().getItem().get(position).getCurCount() + getString(R.string.content));
                Glide.with(getContext()).load(space.getData().getFavourite().getItem().get(position).getCover().get(0).getPic()).into(((FavoriteCardViewHolder) holder).mIv);
                ((FavoriteCardViewHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {

                    private Space.Data.Favourite.Item item = space.getData().getFavourite().getItem().get(position);

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), FavoriteActivity.class);
                        intent.putExtra("item", GsonUtil.getGsonInstance().toJson(item, Space.Data.Favourite.Item.class));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                if (space == null) {
                    return 0;
                }else {
                    return space.getData().getFavourite().getItem().size();
                }
            }

            class FavoriteCardViewHolder extends RecyclerView.ViewHolder {

                private ImageView mIv;
                private TextView mTvName, mTvCount;
                private CardView mCv;

                public FavoriteCardViewHolder(@NonNull View itemView) {
                    super(itemView);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvName = itemView.findViewById(R.id.tv_name);
                    mTvCount = itemView.findViewById(R.id.tv_count);
                    mCv = itemView.findViewById(R.id.cv);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("space", GsonUtil.getGsonInstance().toJson(space));
    }
}
