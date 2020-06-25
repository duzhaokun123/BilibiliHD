package com.duzhaokun123.bilibilihd.ui.userspace;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.MediaListAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model.Ids;
import com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model.Infos;
import com.duzhaokun123.bilibilihd.mybilibiliapi.model.Base;
import com.duzhaokun123.bilibilihd.mybilibiliapi.toview.ToViewAPI;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class FavoriteActivity extends BaseActivity<LayoutXrecyclerviewOnlyBinding> {

    private Ids ids;
    private Infos infos;

    @Override
    protected int initConfig() {
        return NEED_HANDLER | FIX_LAYOUT;
    }

    @Override
    public int initLayout() {
        return R.layout.layout_xrecyclerview_only;
    }

    @Override
    public void initView() {
        if (teleportIntent != null && teleportIntent.getExtras() != null) {
            setTitle(teleportIntent.getExtras().getString("name"));
        }

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
                if (viewType == 0) {
                    return new VideoCardHolder(LayoutInflater.from(FavoriteActivity.this).inflate(R.layout.layout_video_card_item, parent, false));
                } else {
                    return new VHolder(new View(FavoriteActivity.this));
                }
            }

            @Override
            public int getItemViewType(int position) {
                if (position == ids.getData().size()) {
                    return 1;
                } else {
                    return 0;
                }
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (getItemViewType(position) == 1) {
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getFixButtonHeight());
                    holder.itemView.setLayoutParams(params);
                } else {
                    if (infos == null) {
                        ((VideoCardHolder) holder).mTvTitle.setText(ids.getData().get(position).getBvid());
                    } else {
                        ((VideoCardHolder) holder).mTvTitle.setText(infos.getData().get(position).getTitle());
                        ((VideoCardHolder) holder).mTvUp.setText(infos.getData().get(position).getIntro());
                        GlideUtil.loadUrlInto(FavoriteActivity.this, infos.getData().get(position).getCover(), ((VideoCardHolder) holder).mIv, true);
                    }
                    ((VideoCardHolder) holder).mCv.setOnClickListener(v -> {
                        Intent intent = new Intent(FavoriteActivity.this, PlayActivity.class);
                        intent.putExtra("aid", ids.getData().get(position).getId());
                        startActivity(intent);
                    });
                    if (infos != null) {
                        ((VideoCardHolder) holder).mCv.setOnLongClickListener(new View.OnLongClickListener() {

                            private String bvid = ids.getData().get(position).getBvid();
                            private String url = infos.getData().get(position).getCover();

                            @Override
                            public boolean onLongClick(View v) {

                                PopupMenu popupMenu = new PopupMenu(FavoriteActivity.this, ((VideoCardHolder) holder).mCv);
                                popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                                popupMenu.setOnMenuItemClickListener(item -> {
                                    switch (item.getItemId()) {
                                        case R.id.check_cover:
                                            Intent intent = new Intent(FavoriteActivity.this, PhotoViewActivity.class);
                                            intent.putExtra("url", url);
                                            startActivity(intent);
                                            break;
                                        case R.id.add_to_watch_later:
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    ToViewAPI.getInstance().addBvid(bvid, new MyBilibiliClient.ICallback<Base>() {
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
                }
            }

            @Override
            public int getItemCount() {
                if (ids == null) {
                    return 0;
                } else {
                    return ids.getData().size() + 1;
                }
            }

            class VideoCardHolder extends RecyclerView.ViewHolder {

                private CardView mCv;
                private ImageView mIv;
                private TextView mTvTitle, mTvUp;

                VideoCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mCv = itemView.findViewById(R.id.cv);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvTitle = itemView.findViewById(R.id.tv_title);
                    mTvUp = itemView.findViewById(R.id.tv_up);
                    ((RelativeLayout) itemView.findViewById(R.id.rl)).removeView(itemView.findViewById(R.id.civ_face));
                }
            }

            class VHolder extends RecyclerView.ViewHolder {

                public VHolder(@NonNull View itemView) {
                    super(itemView);
                }
            }
        });
        baseBind.xrv.setLoadingMoreEnabled(false);
        baseBind.xrv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Refresh().start();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    public void initData() {
        baseBind.xrv.refresh();
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 1:
                baseBind.xrv.refreshComplete();
            case 0:
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, ids.getData().size());
        }
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            if (teleportIntent != null && teleportIntent.getExtras() != null) {
                MediaListAPI.getInstance().getIds(teleportIntent.getExtras().getLong("media_id"),
                        teleportIntent.getExtras().getLong("mid"), new MyBilibiliClient.ICallback<Ids>() {
                            @Override
                            public void onException(Exception e) {
                                e.printStackTrace();
                                runOnUiThread(() -> ToastUtil.sendMsg(FavoriteActivity.this, e.getMessage()));
                            }

                            @Override
                            public void onSuccess(Ids ids) {
                                FavoriteActivity.this.ids = ids;
                                if (handler != null) {
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        });
                if (ids != null) {
                    MediaListAPI.getInstance().getInfos(teleportIntent.getExtras().getLong("media_id"),
                            teleportIntent.getExtras().getLong("mid"), ids, new MyBilibiliClient.ICallback<Infos>() {
                                @Override
                                public void onException(Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(() -> ToastUtil.sendMsg(FavoriteActivity.this, e.getMessage()));
                                }

                                @Override
                                public void onSuccess(Infos infos) {
                                    FavoriteActivity.this.infos = infos;
                                    if (handler != null) {
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            });
                }
            }
        }
    }
}
