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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutRecyclerviewWithVBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.ObjectCache;

public class FavoriteFragment extends BaseFragment<LayoutRecyclerviewWithVBinding> {


    private Space space;

    public FavoriteFragment(){}

    FavoriteFragment(Space space) {
        this.space = space;
    }

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.layout_recyclerview_with_v;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        space = (Space) ObjectCache.get(savedInstanceState.getLong("space"));
    }

    @Override
    protected void initView() {
        baseBind.rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        baseBind.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
            }
        });
        baseBind.rv.setAdapter(new RecyclerView.Adapter() {
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
                if (space.getData().getFavourite().getItem().get(position).getCover() != null) {
                    Glide.with(requireContext()).load(space.getData().getFavourite().getItem().get(position).getCover().get(0).getPic()).into(((FavoriteCardViewHolder) holder).mIv);

                }
                ((FavoriteCardViewHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {

                    private Space.Data.Favourite.Item item = space.getData().getFavourite().getItem().get(position);

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), FavoriteActivity.class);
                        intent.putExtra("mid", item.getMid());
                        intent.putExtra("media_id", item.getMediaId());
                        intent.putExtra("name", item.getName());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                if (space == null) {
                    return 0;
                } else {
                    return space.getData().getFavourite().getItem().size();
                }
            }

            class FavoriteCardViewHolder extends RecyclerView.ViewHolder {

                private ImageView mIv;
                private TextView mTvName, mTvCount;
                private CardView mCv;

                FavoriteCardViewHolder(@NonNull View itemView) {
                    super(itemView);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvName = itemView.findViewById(R.id.tv_name);
                    mTvCount = itemView.findViewById(R.id.tv_count);
                    mCv = itemView.findViewById(R.id.cv);
                }
            }
        });

        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null && baseActivity.navigationBarOnButton) {
            ViewGroup.LayoutParams params = baseBind.v.getLayoutParams();
            params.height = baseActivity.getFixButtonHeight();
            baseBind.v.setLayoutParams(params);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("space", ObjectCache.put(space));
    }
}
