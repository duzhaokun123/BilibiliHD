package com.duzhaokun123.bilibilihd.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.article.ArticleActivity;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.ui.widget.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.CustomTabUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.hiczp.bilibili.api.app.model.HomePage;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ConcurrentModificationException;

import de.hdodenhof.circleimageview.CircleImageView;

// FIXME: 20-2-24 这么搞线程不安全啊
public class HomeFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> {
    private PBilibiliClient pBilibiliClient = PBilibiliClient.Companion.getInstance();
    private HomePage homePage;

    @Override
    protected void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        homePage = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("homePage"), HomePage.class);
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
                                CustomTabUtil.openUrl(getContext(), homePage.getData().getItems().get(position).getMask().getAvatar().getUri())
                        );
                    } else if (homePage.getData().getItems().get(position).getAdInfo() != null
                            && homePage.getData().getItems().get(position).getAdInfo().getCreativeContent() != null) {
                        GlideUtil.loadUrlInto(getContext(), homePage.getData().getItems().get(position).getAdInfo().getCreativeContent().getImageUrl(), ((VideoCardHolder) holder).mIv, true);
                        ((VideoCardHolder) holder).mTvTitle.setText(homePage.getData().getItems().get(position).getAdInfo().getCreativeContent().getTitle());
                        ((VideoCardHolder) holder).mTvUp.setText(R.string.ad);
                    } else if (homePage.getData().getItems().get(position).getCovers() != null) {
                        GlideUtil.loadUrlInto(getContext(), homePage.getData().getItems().get(position).getCovers().get(0),  ((VideoCardHolder) holder).mIv, true);
                    }
                }
                ((VideoCardHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {

                    private long aid;
                    private String cardGoto = homePage.getData().getItems().get(position).getCardGoto();

                    {
                        try {
                            aid = Long.parseLong(homePage.getData().getItems().get(position).getParam());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        if ("av".equals(cardGoto)) {
                            intent = new Intent(getContext(), PlayActivity.class);
                            intent.putExtra("aid", aid);
                        } else if("article".equals(cardGoto) || "article_s".equals(cardGoto)) {
                            CustomTabUtil.openUrl(getContext(), homePage.getData().getItems().get(position).getUri());
                        } else if ("ad_web_s".equals(cardGoto)) {
                            CustomTabUtil.openUrl(getContext(), homePage.getData().getItems().get(position).getAdInfo().getCreativeContent().getUrl());
                        } else {
                            ToastUtil.sendMsg(getContext(), "不支持 " + cardGoto);
                            CustomTabUtil.openUrl(getContext(), homePage.getData().getItems().get(position).getUri());
                        }
                        if (intent != null) {
                            startActivity(intent);
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
                        }  else if (homePage.getData().getItems().get(position).getCovers() != null) {
                            url =  homePage.getData().getItems().get(position).getCovers().get(0);
                        }
                        try {
                            aid = Long.parseLong(homePage.getData().getItems().get(position).getParam());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(CLASS_NAME, homePage.getData().getItems().get(position).toString());
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
                                    // TODO: 20-2-27
                                    ToastUtil.sendMsg(getContext(), "还没有做");
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

                private ImageView mIv;
                private TextView mTvTitle, mTvCoverLeftText1, mTvCoverLeftText2, mTvCoverLeftText3, mTvUp;
                private CardView mCv;
                private CircleImageView mCivFace;

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
        baseBind.xrv.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
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
    }

    @Override
    protected void initData() {
        if (homePage == null) {
            baseBind.xrv.refresh();
        }
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                baseBind.xrv.refreshComplete();
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, homePage.getData().getItems().size() - 1);
                break;
            case 1:
                baseBind.xrv.loadMoreComplete();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString("homePage", GsonUtil.getGsonInstance().toJson(homePage));
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            try {
                homePage = pBilibiliClient.getPAppAPI().homePage(true);
                for (int i = 0; i < 2; i++) {
                    homePage.getData().getItems().addAll(pBilibiliClient.getPAppAPI().homePage(false).getData().getItems());
                }
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> ToastUtil.sendMsg(getContext(), e.getMessage()));
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
                    getActivity().runOnUiThread(() -> ToastUtil.sendMsg(getContext(), e.getMessage()));
                }
            }
            if (handler != null) {
                handler.sendEmptyMessage(1);
            }
        }
    }
}
