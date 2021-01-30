package com.duzhaokun123.bilibilihd.ui.main;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.databinding.LayoutSrlBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.play.online.OnlinePlayActivity;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.ObjectCache;
import com.duzhaokun123.bilibilihd.utils.Refreshable;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.hiczp.bilibili.api.app.model.HomePage;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment<LayoutSrlBinding> implements Refreshable {
    private PBilibiliClient pBilibiliClient = Application.getPBilibiliClient();
    private HomePage homePage;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        homePage = (HomePage) ObjectCache.get(savedInstanceState.getString("homePage"));
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
        baseBind.rv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public VideoCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VideoCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_video_card_item, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((VideoCardHolder) holder).mTvTitle.setText(homePage.getData().getItems().get(position).getTitle());
                ((VideoCardHolder) holder).mTvCoverLeftText1.setText(homePage.getData().getItems().get(position).getCoverLeftText1());
                ((VideoCardHolder) holder).mTvCoverLeftText2.setText(homePage.getData().getItems().get(position).getCoverLeftText2());
                ((VideoCardHolder) holder).mTvCoverLeftText3.setText(homePage.getData().getItems().get(position).getCoverLeftText3());
                ((VideoCardHolder) holder).mTvUp.setText(homePage.getData().getItems().get(position).getDesc());
                if (getContext() != null) {
                    GlideUtil.loadUrlInto(getContext(), homePage.getData().getItems().get(position).getCover(), ((VideoCardHolder) holder).mIv, true);
                    if (homePage.getData().getItems().get(position).getMask() != null) {
                        GlideUtil.loadUrlInto(getContext(), homePage.getData().getItems().get(position).getMask().getAvatar().getCover(), ((VideoCardHolder) holder).mCivFace, false);
                        ((VideoCardHolder) holder).mCivFace.setOnClickListener(v ->
                                BrowserUtil.openCustomTab(getContext(), homePage.getData().getItems().get(position).getMask().getAvatar().getUri())
                        );
                    } else if (homePage.getData().getItems().get(position).getAdInfo() != null
                            && homePage.getData().getItems().get(position).getAdInfo().getCreativeContent() != null) {
                        GlideUtil.loadUrlInto(getContext(), homePage.getData().getItems().get(position).getAdInfo().getCreativeContent().getImageUrl(), ((VideoCardHolder) holder).mIv, true);
                        ((VideoCardHolder) holder).mTvTitle.setText(homePage.getData().getItems().get(position).getAdInfo().getCreativeContent().getTitle());
                        ((VideoCardHolder) holder).mTvUp.setText(R.string.ad);
                    } else if (homePage.getData().getItems().get(position).getCovers() != null) {
                        GlideUtil.loadUrlInto(getContext(), homePage.getData().getItems().get(position).getCovers().get(0), ((VideoCardHolder) holder).mIv, true);
                    }
                }
                ((VideoCardHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {

                    private long aid;
                    private String cardGoto = homePage.getData().getItems().get(position).getCardGoto();

                    {
                        try {
                            aid = Long.parseLong(homePage.getData().getItems().get(position).getParam());
                        } catch (Exception e) {
                            //ignore
                        }
                    }

                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        Bundle options = null;
                        if ("av".equals(cardGoto)) {
                            intent = new Intent(getContext(), OnlinePlayActivity.class);
                            intent.putExtra("aid", aid);
                            intent.putExtra(OnlinePlayActivity.EXTRA_FAST_LOAD_COVER_URL, homePage.getData().getItems().get(position).getCover());
                            options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), ((VideoCardHolder) holder).mIv, "cover").toBundle();
                        } else if ("article".equals(cardGoto) || "article_s".equals(cardGoto)) {
                            BrowserUtil.openCustomTab(getContext(), homePage.getData().getItems().get(position).getUri());
                        } else if ("ad_web_s".equals(cardGoto)) {
                            BrowserUtil.openCustomTab(getContext(), Objects.requireNonNull(homePage.getData().getItems().get(position).getAdInfo()).getCreativeContent().getUrl());
                        } else {
                            TipUtil.showToast("不支持 " + cardGoto);
                            BrowserUtil.openCustomTab(getContext(), homePage.getData().getItems().get(position).getUri());
                        }
                        if (intent != null) {
                            startActivity(intent, options);
                        }
                    }
                });
                ((VideoCardHolder) holder).mCv.setOnLongClickListener(new View.OnLongClickListener() {

                    private long aid;
                    private String url;

                    {
                        url = homePage.getData().getItems().get(position).getCover();
                        if (homePage.getData().getItems().get(position).getAdInfo() != null
                                && homePage.getData().getItems().get(position).getAdInfo().getCreativeContent() != null) {
                            url = homePage.getData().getItems().get(position).getAdInfo().getCreativeContent().getImageUrl();
                        } else if (homePage.getData().getItems().get(position).getCovers() != null) {
                            url = homePage.getData().getItems().get(position).getCovers().get(0);
                        }
                        try {
                            aid = Long.parseLong(homePage.getData().getItems().get(position).getParam());
                        } catch (Exception e) {
                            //ignore
                        }
                    }

                    @Override
                    public boolean onLongClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(getContext(), ((VideoCardHolder) holder).mCv);
                        popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.check_cover:
                                    ImageViewUtil.INSTANCE.viewImage(requireContext(), url, ((VideoCardHolder) holder).mIv, true);
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
                if (homePage == null) {
                    return 0;
                } else {
                    return homePage.getData().getItems().size();
                }
            }

            class VideoCardHolder extends RecyclerView.ViewHolder {

                final private ImageView mIv;
                final private TextView mTvTitle, mTvCoverLeftText1, mTvCoverLeftText2, mTvCoverLeftText3, mTvUp;
                final private CardView mCv;
                final private CircleImageView mCivFace;

                VideoCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvTitle = itemView.findViewById(R.id.tv_title);
                    mTvCoverLeftText1 = itemView.findViewById(R.id.tv_cover_left_text_1);
                    mTvCoverLeftText2 = itemView.findViewById(R.id.tv_cover_left_text_2);
                    mTvCoverLeftText3 = itemView.findViewById(R.id.tv_cover_left_text_3);
                    mTvUp = itemView.findViewById(R.id.tv_up);
                    mCv = itemView.findViewById(R.id.cv);
                    mCivFace = itemView.findViewById(R.id.civ_face);
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
    }

    @Override
    protected void initData() {
        if (homePage == null) {
            baseBind.srl.autoRefresh();
        }
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                baseBind.srl.finishRefresh();
                Objects.requireNonNull(baseBind.rv.getAdapter()).notifyItemRangeChanged(0, homePage.getData().getItems().size());
                break;
            case 1:
                baseBind.srl.finishLoadMore();
                Objects.requireNonNull(baseBind.rv.getAdapter()).notifyItemRangeChanged(0, homePage.getData().getItems().size());
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("homePage", ObjectCache.put(homePage));
    }

    @Override
    public void onRefresh() {
        baseBind.srl.autoRefresh();
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            try {
                homePage = pBilibiliClient.getPAppAPI().homePage(true);
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        TipUtil.showTip(getContext(), e.getMessage());
                        baseBind.srl.finishRefresh(false);
                    });
                }
            }
        }
    }

    class LoadMore extends Thread {
        @Override
        public void run() {
            try {
                homePage.getData().getItems().addAll(pBilibiliClient.getPAppAPI().homePage(false).getData().getItems());
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        TipUtil.showTip(getContext(), e.getMessage());
                        baseBind.srl.finishLoadMore(false);
                    });
                }
            }
            if (handler != null) {
                handler.sendEmptyMessage(1);
            }
        }
    }
}
