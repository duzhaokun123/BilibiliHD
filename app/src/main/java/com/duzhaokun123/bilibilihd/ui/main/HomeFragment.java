package com.duzhaokun123.bilibilihd.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.hiczp.bilibili.api.app.model.HomePage;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

// FIXME: 20-2-24 这么搞线程不安全啊
public class HomeFragment extends Fragment {

    private XRecyclerView mXrv;

    private PBilibiliClient pBilibiliClient;
    private HomePage homePage;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        View view = inflater.inflate(R.layout.fragment_only_xrecyclerview, container, false);
        mXrv = view.findViewById(R.id.xrv);
        mXrv.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.column_medium), StaggeredGridLayoutManager.VERTICAL));
        if (getResources().getInteger(R.integer.column_medium) == 1) {
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
                return new VideoCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_video_card_item, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((VideoCardHolder) holder).mTvTitle.setText(homePage.getData().getItems().get(position).getTitle());
                ((VideoCardHolder) holder).mTvCount.setText(homePage.getData().getItems().get(position).getCoverLeftText1() + getString(R.string.watched));
                ((VideoCardHolder) holder).mTvDuration.setText(homePage.getData().getItems().get(position).getCoverRightText());
                Glide.with(((VideoCardHolder) holder).mIv).load(homePage.getData().getItems().get(position).getCover()).listener(new RequestListener<Drawable>() {
                    private ImageView imageView = ((VideoCardHolder) holder).mIv;
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        params.height = imageView.getWidth() / resource.getIntrinsicWidth() * resource.getIntrinsicHeight() + imageView.getPaddingBottom() + imageView.getPaddingTop();
//                        imageView.setMaxHeight(params.height);
                        imageView.setLayoutParams(params);
                        return false;
                    }
                }).into(((VideoCardHolder) holder).mIv);
                ((VideoCardHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {

                    private String av = homePage.getData().getItems().get(position).getParam();

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PlayActivity.class);
                        intent.putExtra("av", av);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                if (homePage == null) {
                    return 0;
                } else {
                    return homePage.getData().getItems().size();
                }
            }

            class VideoCardHolder extends RecyclerView.ViewHolder {

                private ImageView mIv;
                private TextView mTvTitle, mTvCount, mTvDuration;
                private CardView mCv;

                public VideoCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvTitle = itemView.findViewById(R.id.tv_title);
                    mTvCount = itemView.findViewById(R.id.tv_count);
                    mTvDuration = itemView.findViewById(R.id.tv_duration);
                    mCv = itemView.findViewById(R.id.cv);
                }
            }
        });
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
        mXrv.setBackgroundResource(android.R.color.white);
        new Thread(new Refresh()).start();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class Handler extends android.os.Handler {
        private int i = 1;
        /**
         * msg.what 0: 下拉刷新 1: 加载更多
         * @param msg
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    i = 1;
                    mXrv.refreshComplete();
                    XRecyclerViewUtil.notifyItemsChanged(mXrv, homePage.getData().getItems().size() - 1);
                    break;
                case 1:
                    i++;
                    mXrv.loadMoreComplete();
                    mXrv.setLimitNumberToCallLoadMore(i);
                    break;
            }
        }
    }

    class Refresh implements Runnable {
        @Override
        public void run() {
            try {
                homePage = pBilibiliClient.getPAppAPI().homePage(true);
            } catch (Exception e) {
                e.printStackTrace();
                Looper.prepare();
                ToastUtil.sendMsg(getContext(), e.getMessage());
                Looper.loop();
            }
            handler.sendEmptyMessage(0);
        }
    }

    class LoadMore implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("HomePage", "LoadMore");
                homePage = pBilibiliClient.getPAppAPI().homePage(false);
            } catch (Exception e) {
                e.printStackTrace();
                Looper.prepare();
                ToastUtil.sendMsg(getContext(), e.getMessage());
                Looper.loop();
            }
            handler.sendEmptyMessage(1);
        }
    }
}
