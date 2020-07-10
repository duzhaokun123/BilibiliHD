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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.model.Base;
import com.duzhaokun123.bilibilihd.mybilibiliapi.toview.ToViewAPI;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.HistoryAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.model.History;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class HistoryFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> {

    private History mHistory;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mHistory = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("mHistory"), History.class);
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
        baseBind.xrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VideoCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_video_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((VideoCardHolder) holder).mTvTitle.setText(mHistory.getData().getList().get(position).getTitle());
                GlideUtil.loadUrlInto(getContext(), mHistory.getData().getList().get(position).getCover(), ((VideoCardHolder) holder).mIv, true);
                if (mHistory.getData().getList().get(position).getCovers() != null) {
                    GlideUtil.loadUrlInto(getContext(), mHistory.getData().getList().get(position).getCovers().get(0), ((VideoCardHolder) holder).mIv, true);
                }
                ((VideoCardHolder) holder).mTvBadge.setText(mHistory.getData().getList().get(position).getBadge());
                ((VideoCardHolder) holder).mTvUp.setText(mHistory.getData().getList().get(position).getName());
                ((VideoCardHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {

                    private String url = mHistory.getData().getList().get(position).getUri();

                    @Override
                    public void onClick(View v) {
                        BrowserUtil.openCustomTab(requireContext(), url);
                    }
                });
                ((VideoCardHolder) holder).mCv.setOnLongClickListener(new View.OnLongClickListener() {

                    private long aid = mHistory.getData().getList().get(position).getHistory().getOid();
                    private String url;

                    {
                        url = mHistory.getData().getList().get(position).getCover();
                        if (mHistory.getData().getList().get(position).getCovers() != null) {
                            url = mHistory.getData().getList().get(position).getCovers().get(0);
                        }
                    }

                    @Override
                    public boolean onLongClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(getContext(), ((VideoCardHolder) holder).mCv);
                        popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.check_cover:
                                    Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                                    intent.putExtra("url", url);
                                    startActivity(intent);
                                    break;
                                case R.id.add_to_watch_later:
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            ToViewAPI.getInstance().addAid(aid, new MyBilibiliClient.ICallback<Base>() {
                                                @Override
                                                public void onException(Exception e) {
                                                    e.printStackTrace();
                                                }

                                                @Override
                                                public void onSuccess(Base base) {

                                                }
                                            });
                                        }
                                    }.start();
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

            class VideoCardHolder extends RecyclerView.ViewHolder {

                private ImageView mIv;
                private TextView mTvTitle, mTvUp, mTvBadge;
                private CardView mCv;


                VideoCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvTitle = itemView.findViewById(R.id.tv_title);
                    mTvUp = itemView.findViewById(R.id.tv_up);
                    mTvBadge = itemView.findViewById(R.id.tv_badge);
                    mCv = itemView.findViewById(R.id.cv);
                    ((RelativeLayout) itemView.findViewById(R.id.rl)).removeView(itemView.findViewById(R.id.civ_face));
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
        outState.putString("mHistory", GsonUtil.getGsonInstance().toJson(mHistory));
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            HistoryAPI.getInstance().getHistory("all", new MyBilibiliClient.ICallback<History>() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    if (handler != null) {
                        handler.sendEmptyMessage(2);
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                    }
                }

                @Override
                public void onSuccess(History history) {
                    mHistory = history;
                    LoadMore loadMore = new LoadMore();
                    for (int i = 0; i < 2; i++) {
                        loadMore.run();
                    }
                    if (handler != null) {
                        handler.sendEmptyMessage(0);
                    }
                }
            });
        }
    }

    class LoadMore extends Thread {
        @Override
        public void run() {
            HistoryAPI.getInstance().getHistory(mHistory.getData().getCursor().getMax(), mHistory.getData().getCursor().getMax_tp(),
                    "all", new MyBilibiliClient.ICallback<History>() {
                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                            if (handler != null) {
                                handler.sendEmptyMessage(2);
                            }
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                            }
                        }

                        @Override
                        public void onSuccess(History history) {
                            mHistory.getData().getCursor().setMax(history.getData().getCursor().getMax());
                            mHistory.getData().getCursor().setMax_tp(history.getData().getCursor().getMax_tp());
                            mHistory.getData().getList().addAll(history.getData().getList());
                            if (handler != null) {
                                handler.sendEmptyMessage(1);
                            }
                        }
                    });
        }
    }
}
