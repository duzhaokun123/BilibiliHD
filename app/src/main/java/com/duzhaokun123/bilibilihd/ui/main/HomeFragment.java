package com.duzhaokun123.bilibilihd.ui.main;

import android.annotation.SuppressLint;
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
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
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

    private int limitNumber = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        SettingsManager settingsManager = SettingsManager.getSettingsManager();
        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        View view = inflater.inflate(R.layout.fragment_xrecyclerview_only, container, false);
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

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((VideoCardHolder) holder).mTvTitle.setText(homePage.getData().getItems().get(position).getTitle());
                ((VideoCardHolder) holder).mTvCount.setText(homePage.getData().getItems().get(position).getCoverLeftText1() + getString(R.string.watched));
                ((VideoCardHolder) holder).mTvDuration.setText(homePage.getData().getItems().get(position).getCoverRightText());
                ((VideoCardHolder) holder).mTvBadge.setText(homePage.getData().getItems().get(position).getBadge());
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

                    private String aid = homePage.getData().getItems().get(position).getParam();
                    private String badge = homePage.getData().getItems().get(position).getBadge();

                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        if (badge == null) {
                            intent = new Intent(getContext(), PlayActivity.class);
                            intent.putExtra("aid", aid);
                            startActivity(intent);
                        } else {
                            ToastUtil.sendMsg(getContext(), "暂不支持 " + badge);
                        }

                    }
                });
                ((VideoCardHolder) holder).mCv.setOnLongClickListener(new View.OnLongClickListener() {

                    private String aid = homePage.getData().getItems().get(position).getParam();
                    private String url = homePage.getData().getItems().get(position).getCover();

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
                if (homePage == null) {
                    return 0;
                } else {
                    return homePage.getData().getItems().size();
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
        mXrv.setBackgroundResource(android.R.color.white);
        new Thread(new Refresh()).start();
        return view;
    }

    class Handler extends android.os.Handler {
        /**
         * msg.what 0: 下拉刷新 1: 加载更多
         * @param msg
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    mXrv.refreshComplete();
                    XRecyclerViewUtil.notifyItemsChanged(mXrv, homePage.getData().getItems().size() - 1);
                    break;
                case 1:
                    mXrv.loadMoreComplete();
                    break;
            }
        }
    }

    class Refresh implements Runnable {
        @Override
        public void run() {
            Log.d("HomePage", "Refresh");
            limitNumber = 1;
            try {
                homePage = pBilibiliClient.getPAppAPI().homePage(true);
                for (int i = 0; i < 2; i++) {
                    homePage.getData().getItems().addAll(pBilibiliClient.getPAppAPI().homePage(false).getData().getItems());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Looper.prepare();
                if (getContext() != null) {
                    ToastUtil.sendMsg(getContext(), e.getMessage());
                }
                Looper.loop();
            }
            handler.sendEmptyMessage(0);
        }
    }

    class LoadMore implements Runnable {
        @Override
        public void run() {
            Log.d("HomePage", "LoadMore");
            limitNumber++;
            mXrv.setLimitNumberToCallLoadMore(limitNumber);
            try {
                homePage.getData().getItems().addAll(pBilibiliClient.getPAppAPI().homePage(false).getData().getItems());
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
