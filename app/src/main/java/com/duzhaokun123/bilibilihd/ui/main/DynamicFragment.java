package com.duzhaokun123.bilibilihd.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.myBilibiliApi.dynamic.DynamicAPI;
import com.duzhaokun123.bilibilihd.myBilibiliApi.dynamic.model.DynamicPage;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.userSpace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DynamicFragment extends Fragment {

    private XRecyclerView mXrv;

    private Handler handler;
    private List<DynamicPage.Data.Card> mCards;

    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        SettingsManager settingsManager = SettingsManager.getSettingsManager();
        View view = inflater.inflate(R.layout.fragment_only_xrecyclerview, container, false);
        mXrv = view.findViewById(R.id.xrv);
        int spanCount = getResources().getInteger(R.integer.column_medium);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && settingsManager.layout.getColumn() != 0) {
            spanCount = settingsManager.layout.getColumn();
        } else if (settingsManager.layout.getColumnLand() != 0) {
            spanCount = settingsManager.layout.getColumnLand();
        }
        mXrv.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
        if (spanCount == 1) {
            mXrv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
                }
            });
        } else {
            mXrv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height), getResources().getDimensionPixelOffset(R.dimen.divider_height));
                }
            });
        }
        mXrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                RecyclerView.ViewHolder viewHolder = new DynamicCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_dynamic_card_item, parent, false));
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Glide.with(mXrv).load(mCards.get(position).getDesc().getUser_profile().getInfo().getFace()).into(((DynamicCardHolder) holder).mCivFace);
                ((DynamicCardHolder) holder).mTvName.setText(mCards.get(position).getDesc().getUser_profile().getInfo().getUname());
                ((DynamicCardHolder) holder).mTvTime.setText(String.valueOf(mCards.get(position).getDesc().getTimestamp()));
                ((DynamicCardHolder) holder).mTvContent.setText(mCards.get(position).getCard());
                if (mCards.get(position).getDesc().getUser_profile().getVip().getVipType() != 1) {//1: 不是 VIP, 2: 是 VIP
                    // FIXME: 20-2-26 为什么在这里设置所有都会变
                    ((DynamicCardHolder) holder).mTvName.setTextColor(getContext().getColor(R.color.colorAccent));
                }

                OpenUserSpace openUserSpace = new OpenUserSpace(mCards.get(position).getDesc().getUid());
                ((DynamicCardHolder) holder).mCivFace.setOnClickListener(openUserSpace);
                ((DynamicCardHolder) holder).mTvName.setOnClickListener(openUserSpace);
            }

            @Override
            public int getItemCount() {
                if(mCards == null) {
                    return 0;
                } else {
                    return mCards.size();
                }
            }

            class DynamicCardHolder extends RecyclerView.ViewHolder{

                private CircleImageView mCivFace;
                private TextView mTvName, mTvTime, mTvContent;
                private ImageView mIvPhoto1;

                public DynamicCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mCivFace = itemView.findViewById(R.id.cli_face);
                    mTvName = itemView.findViewById(R.id.tv_name);
                    mTvTime = itemView.findViewById(R.id.tv_time);
                    mTvContent = itemView.findViewById(R.id.tv_content);
                    mIvPhoto1 = itemView.findViewById(R.id.iv_photo_1);
                }
            }

            class OpenUserSpace implements View.OnClickListener {

                private long uid;

                public OpenUserSpace(long uid) {
                    this.uid = uid;
                }

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UserSpaceActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            }
        });
        mXrv.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        mXrv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Thread(new Refresh()).start();
            }

            @Override
            public void onLoadMore() {
                new Thread(new LoadMore()).start();
            }
        });
        new Thread(new Refresh()).start();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    mXrv.refreshComplete();
                    XRecyclerViewUtil.notifyItemsChanged(mXrv, mCards.size() - 1);
                    break;
                case 1:
                    mXrv.loadMoreComplete();
                    break;
            }
        }
    }

    class Refresh implements Runnable{
        @Override
        public void run() {
            page = 1;
            DynamicAPI.getDynamicAPI().getDynamic(page, new DynamicAPI.Callback() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    ToastUtil.sendMsg(getContext(), e.getMessage());
                }

                @Override
                public void onSuccess(DynamicPage dynamicPage) {
                     mCards = dynamicPage.getData().getCards();
                     handler.sendEmptyMessage(0);
                }
            });
        }
    }

    class LoadMore implements Runnable{
        @Override
        public void run() {
            DynamicAPI.getDynamicAPI().getDynamic(page, new DynamicAPI.Callback() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    ToastUtil.sendMsg(getContext(), e.getMessage());
                }

                @Override
                public void onSuccess(DynamicPage dynamicPage) {
                    mCards.addAll(dynamicPage.getData().getCards());
                    handler.sendEmptyMessage(1);
                }
            });
        }
    }
}
