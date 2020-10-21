package com.duzhaokun123.bilibilihd.ui.main;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.ObjectCache;
import com.duzhaokun123.bilibilihd.utils.Refreshable;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.hiczp.bilibili.api.app.model.History;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.Objects;

public class HistoryFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> implements Refreshable {

    private History mHistory;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mHistory = (History) ObjectCache.get(savedInstanceState.getString("mHistory"));
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    protected int initLayout() {
        return R.layout.layout_xrecyclerview_only;
    }

    @Override
    protected void initView() {
        int spanCount = getResources().getInteger(R.integer.column_medium);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && Settings.layout.getColumn() != 0) {
            spanCount = Settings.layout.getColumn();
        } else if (Settings.layout.getColumnLand() != 0) {
            spanCount = Settings.layout.getColumnLand();
        }
        baseBind.xrv.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
        if (spanCount == 1) {
            baseBind.xrv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
                }
            });
        } else {
            baseBind.xrv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height), getResources().getDimensionPixelOffset(R.dimen.divider_height));
                }
            });
        }
        baseBind.xrv.setAdapter(new RecyclerView.Adapter<VideoCardHolder>() {
            @NonNull
            @Override
            public VideoCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VideoCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_video_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull VideoCardHolder holder, int position) {
                holder.pb.setVisibility(View.GONE);
                holder.mTvTitle.setText(mHistory.getData().getList().get(position).getTitle());
                GlideUtil.loadUrlInto(getContext(), mHistory.getData().getList().get(position).getCover(), holder.mIv, true);
                if (mHistory.getData().getList().get(position).getCovers() != null) {
                    GlideUtil.loadUrlInto(getContext(), Objects.requireNonNull(mHistory.getData().getList().get(position).getCovers()).get(0), holder.mIv, true);
                }
                holder.mTvBadge.setText(mHistory.getData().getList().get(position).getBadge());
                holder.mTvUp.setText(mHistory.getData().getList().get(position).getName());
                if (mHistory.getData().getList().get(position).getDuration() != 0) {
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.pb.setMax(mHistory.getData().getList().get(position).getDuration());
                    holder.pb.setProgress(mHistory.getData().getList().get(position).getProgress());
                }
                holder.mCv.setOnClickListener(new View.OnClickListener() {

                    private String url = mHistory.getData().getList().get(position).getUri();

                    @Override
                    public void onClick(View v) {
                        BrowserUtil.openCustomTab(requireContext(), url);
                    }
                });
                holder.mCv.setOnLongClickListener(new View.OnLongClickListener() {

                    private long aid = mHistory.getData().getList().get(position).getHistory().getOid();
                    private String url;

                    {
                        url = mHistory.getData().getList().get(position).getCover();
                        if (mHistory.getData().getList().get(position).getCovers() != null) {
                            url = Objects.requireNonNull(mHistory.getData().getList().get(position).getCovers()).get(0);
                        }
                    }

                    @Override
                    public boolean onLongClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(requireContext(), holder.mCv);
                        popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.check_cover:
                                    ImageViewUtil.INSTANCE.viewImage(requireContext(), url, holder.mIv);
                                    break;
                                case R.id.add_to_watch_later:
                                    new Thread(() -> {
                                        try {
                                            Application.getPBilibiliClient().getPMainAPI().toView(aid, null);
                                            Application.runOnUiThread(() -> TipUtil.showTip(getContext(), R.string.added));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Application.runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                                        }
                                    }).start();
                                    break;
                            }
                            return true;
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
        });
        baseBind.xrv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Refresh().start();
            }

            @Override
            public void onLoadMore() {
                new LoadMore().start();
            }
        });
        baseBind.xrv.setPullRefreshEnabled(true);
        baseBind.xrv.setLoadingMoreEnabled(true);
        baseBind.xrv.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    }

    @Override
    protected void initData() {
        if (mHistory == null) {
            baseBind.xrv.refresh();
        }
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                baseBind.xrv.refreshComplete();
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, mHistory.getData().getList().size() - 1);
                break;
            case 1:
                baseBind.xrv.loadMoreComplete();
                break;
            case 2:
                baseBind.xrv.refreshComplete();
                baseBind.xrv.loadMoreComplete();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mHistory", ObjectCache.put(mHistory));
    }

    @Override
    public void onRefresh() {
        baseBind.xrv.refresh();
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            try {
                mHistory = Application.getPBilibiliClient().getPAppAPI().history("all", 0, 0);
                LoadMore loadMore = new LoadMore();
                for (int i = 0; i < 2; i++) {
                    loadMore.run();
                }
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (handler != null) {
                    handler.sendEmptyMessage(2);
                }
                Application.runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
            }
        }
    }

    class LoadMore extends Thread {
        @Override
        public void run() {
            try {
                History history = Application.getPBilibiliClient().getPAppAPI().history("all", mHistory.getData().getCursor().getMax(), mHistory.getData().getCursor().getMaxTp());
                mHistory.getData().getCursor().setMax(history.getData().getCursor().getMax());
                mHistory.getData().getCursor().setMaxTp(history.getData().getCursor().getMaxTp());
                mHistory.getData().getList().addAll(history.getData().getList());
                if (handler != null) {
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (handler != null) {
                    handler.sendEmptyMessage(2);
                }
                Application.runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
            }
        }
    }

    static class VideoCardHolder extends RecyclerView.ViewHolder {

        private ImageView mIv;
        private TextView mTvTitle, mTvUp, mTvBadge;
        private CardView mCv;
        private ProgressBar pb;

        VideoCardHolder(@NonNull View itemView) {
            super(itemView);
            mIv = itemView.findViewById(R.id.iv);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvUp = itemView.findViewById(R.id.tv_up);
            mTvBadge = itemView.findViewById(R.id.tv_badge);
            mCv = itemView.findViewById(R.id.cv);
            ((RelativeLayout) itemView.findViewById(R.id.rl)).removeView(itemView.findViewById(R.id.civ_face));
            pb = itemView.findViewById(R.id.pb);
        }
    }
}
