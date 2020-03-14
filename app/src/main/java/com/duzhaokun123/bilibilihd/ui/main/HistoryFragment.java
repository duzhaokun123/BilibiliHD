package com.duzhaokun123.bilibilihd.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.Util;
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.HistoryApi;
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.model.History;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class HistoryFragment extends Fragment {

    private XRecyclerView mXrv;

    private Handler handler;
    private History mHistory;
    private int page;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        SettingsManager settingsManager = SettingsManager.getSettingsManager();
        View view = inflater.inflate(R.layout.layout_xrecyclerview_only, container, false);
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
                return new VideoCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_video_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((VideoCardHolder) holder).mTvTitle.setText(mHistory.getData().getList().get(position).getTitle());
                Glide.with(((VideoCardHolder) holder).mIv).load(mHistory.getData().getList().get(position).getCover()).listener(new RequestListener<Drawable>() {
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

                    private long aid = Util.getAidFromBilibiliLink(mHistory.getData().getList().get(position).getUri());

                    @Override
                    public void onClick(View v) {
                        Log.d("aid", String.valueOf(aid));
                        Intent intent = new Intent(getContext(), PlayActivity.class);
                        intent.putExtra("aid", aid);
                        startActivity(intent);
                    }
                });
                ((VideoCardHolder) holder).mCv.setOnLongClickListener(new View.OnLongClickListener() {

                    private long aid = Util.getAidFromBilibiliLink(mHistory.getData().getList().get(position).getUri());
                    private String url = mHistory.getData().getList().get(position).getCover();

                    @Override
                    public boolean onLongClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(getContext(), ((VideoCardHolder) holder).mCv);
                        popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.check_cover:
                                        Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                                        intent.putExtra("url", url);
                                        startActivity(intent);
                                        break;
                                    case R.id.add_to_watch_later:
                                        // TODO: 20-2-27
                                        ToastUtil.sendMsg(getContext(), "还没有做");
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                });
            }

            @Override
            public int getItemCount() {
                if (mHistory == null) {
                    return 0;
                } else {
                    return mHistory.getData().getList().size();
                }
            }

            class VideoCardHolder extends RecyclerView.ViewHolder {

                private ImageView mIv;
                private TextView mTvTitle, mTvCount, mTvDuration, mTvBadge;
                private CardView mCv;

                public VideoCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvTitle = itemView.findViewById(R.id.tv_title);
                    mTvCount = itemView.findViewById(R.id.tv_count);
                    mTvDuration = itemView.findViewById(R.id.tv_duration);
                    mTvBadge =  itemView.findViewById(R.id.tv_badge);
                    mCv = itemView.findViewById(R.id.cv);
                }
            }
        });
        mXrv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Refresh().start();
            }

            @Override
            public void onLoadMore() {
                new LoadMore().start();
            }
        });
        mXrv.setPullRefreshEnabled(true);
        mXrv.setLoadingMoreEnabled(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Refresh().start();
    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    mXrv.refreshComplete();
                    XRecyclerViewUtil.notifyItemsChanged(mXrv, mHistory.getData().getList().size() - 1);
                    break;
                case 1:
                    mXrv.loadMoreComplete();
                    break;
            }
        }
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            page = 1;
            HistoryApi.getHistoryApi().getHistory("all", new MyBilibiliClient.Callback<History>() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    ToastUtil.sendMsg(getContext(), e.getMessage());
                    Looper.loop();
                }

                @Override
                public void onSuccess(History history) {
                    mHistory = history;
                    LoadMore loadMore = new LoadMore();
                    for (int i = 0; i < 2; i++) {
                        loadMore.run();
                    }
                    handler.sendEmptyMessage(0);
                }
            });
        }
    }

    class LoadMore extends Thread {
        @Override
        public void run() {
            page ++;
            HistoryApi.getHistoryApi().getHistory(mHistory.getData().getCursor().getMax(), mHistory.getData().getCursor().getMax_tp(),
                    "all", new MyBilibiliClient.Callback<History>() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    ToastUtil.sendMsg(getContext(), e.getMessage());
                    Looper.loop();
                }

                @Override
                public void onSuccess(History history) {
                    mHistory.getData().getCursor().setMax(history.getData().getCursor().getMax());
                    mHistory.getData().getCursor().setMax_tp(history.getData().getCursor().getMax_tp());
                    mHistory.getData().getList().addAll(history.getData().getList());
                    handler.sendEmptyMessage(1);
                }
            });
        }
    }
}
