package com.duzhaokun123.bilibilihd.ui.userspace;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class FavoriteActivity extends BaseActivity {

    private XRecyclerView mXrv;

    private Space.Data.Favourite.Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mXrv = findViewById(R.id.xrv);
        item = GsonUtil.getGsonInstance().fromJson(getIntent().getStringExtra("item"), Space.Data.Favourite.Item.class);

        setTitle(item.getName());

        int spanCount = getResources().getInteger(R.integer.column_medium);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && Settings.layout.getColumn() != 0) {
            spanCount = Settings.layout.getColumn();
        } else if (Settings.layout.getColumnLand() != 0) {
            spanCount = Settings.layout.getColumnLand();
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
                return new VideoCardHolder(LayoutInflater.from(FavoriteActivity.this).inflate(R.layout.layout_video_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                GlideUtil.loadUrlInto(FavoriteActivity.this, item.getCover().get(0).getPic(), ((VideoCardHolder) holder).mIv, false);
                ((VideoCardHolder) holder).mTvTitle.setText("av" + item.getCover().get(position).getAid());
                ((VideoCardHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {

                    private long aid = Long.parseLong(String.valueOf(item.getCover().get(position).getAid()));

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FavoriteActivity.this, PlayActivity.class);
                        intent.putExtra("aid", aid);
                        startActivity(intent);
                    }
                });
                ((VideoCardHolder) holder).mCv.setOnLongClickListener(new View.OnLongClickListener() {

                    private long aid = Long.parseLong(String.valueOf(item.getCover().get(position).getAid()));
                    private String url = item.getCover().get(position).getPic();

                    @Override
                    public boolean onLongClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(FavoriteActivity.this, ((VideoCardHolder) holder).mCv);
                        popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.check_cover:
                                        Intent intent = new Intent(FavoriteActivity.this, PhotoViewActivity.class);
                                        intent.putExtra("url", url);
                                        startActivity(intent);
                                        break;
                                    case R.id.add_to_watch_later:
                                        // TODO: 20-2-27
                                        ToastUtil.sendMsg(FavoriteActivity.this, "还没有做");
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
                return item.getCover().size();
            }

            class VideoCardHolder extends RecyclerView.ViewHolder {

                private CardView mCv;
                private ImageView mIv;
                private TextView mTvTitle;

                public VideoCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mCv =itemView.findViewById(R.id.cv);
                    mIv = itemView.findViewById(R.id.iv);
                    mTvTitle = itemView.findViewById(R.id.tv_title);
                }
            }
        });
        mXrv.setLoadingMoreEnabled(false);
        mXrv.setPullRefreshEnabled(false);
    }

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    public int initLayout() {
        return R.layout.layout_xrecyclerview_only;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
