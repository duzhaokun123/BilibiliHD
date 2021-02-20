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
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.databinding.LayoutSrlBinding;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.ObjectCache;
import com.duzhaokun123.bilibilihd.utils.Refreshable;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.duzhaokun123.bilibilihd.utils.WindowInsetsCompatKt;
import com.hiczp.bilibili.api.app.model.History;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Objects;

public class HistoryFragment extends BaseFragment<LayoutSrlBinding> implements Refreshable {

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
        return R.layout.layout_srl;
    }

    @Override
    protected void initView() {
        int spanCount = getResources().getInteger(R.integer.column_medium);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && Settings.layout.getColumn() != 0) {
            spanCount = Settings.layout.getColumn();
        } else if (Settings.layout.getColumnLand() != 0) {
            spanCount = Settings.layout.getColumnLand();
        }
        baseBind.rv.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
        if (spanCount == 1) {
            baseBind.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
                }
            });
        } else {
            baseBind.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height), getResources().getDimensionPixelOffset(R.dimen.divider_height));
                }
            });
        }
        baseBind.rv.setAdapter(new RecyclerView.Adapter<VideoCardHolder>() {
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

                    private final String url = mHistory.getData().getList().get(position).getUri();

                    @Override
                    public void onClick(View v) {
                        BrowserUtil.openCustomTab(requireContext(), url);
                    }
                });
                holder.mCv.setOnLongClickListener(new View.OnLongClickListener() {

                    private final long aid = mHistory.getData().getList().get(position).getHistory().getOid();
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
                                    ImageViewUtil.INSTANCE.viewImage(requireContext(), url, holder.mIv, true);
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
        baseBind.srl.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                new LoadMore().start();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                new Refresh().start();
            }
        });

        requireBaseActivity2().registerOnApplyWindowInsets(2, windowInsetsCompat -> {
            int fth;
            int fbh;
            Insets a = WindowInsetsCompatKt.getSystemBars(windowInsetsCompat);
            fth = a.top;
            fbh = a.bottom;
            baseBind.srl.setPadding(0, fth, 0, fbh);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) baseBind.cf.getLayoutParams();
            lp.topMargin = -1 * fbh;
            baseBind.cf.setLayoutParams(lp);
            lp = (ViewGroup.MarginLayoutParams) baseBind.mh.getLayoutParams();
            lp.topMargin = fth;
            baseBind.mh.setLayoutParams(lp);
            return null;
        });
    }

    @Override
    protected void initData() {
        if (mHistory == null) {
            baseBind.srl.autoRefresh();
        }
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                baseBind.srl.finishRefresh();
                Objects.requireNonNull(baseBind.rv.getAdapter()).notifyItemRangeChanged(0, mHistory.getData().getList().size());
                break;
            case 1:
                baseBind.srl.finishLoadMore();
                Objects.requireNonNull(baseBind.rv.getAdapter()).notifyItemRangeChanged(0, mHistory.getData().getList().size());
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
        baseBind.srl.autoRefresh();
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            try {
                mHistory = Application.getPBilibiliClient().getPAppAPI().history("all", 0, 0);
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Application.runOnUiThread(() -> {
                    TipUtil.showTip(getContext(), e.getMessage());
                    baseBind.srl.finishRefresh(false);
                });
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
                Application.runOnUiThread(() -> {
                    TipUtil.showTip(getContext(), e.getMessage());
                    baseBind.srl.finishLoadMore(false);
                });
            }
        }
    }

    static class VideoCardHolder extends RecyclerView.ViewHolder {

        final private ImageView mIv;
        final private TextView mTvTitle, mTvUp, mTvBadge;
        final private CardView mCv;
        final private ProgressBar pb;

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
