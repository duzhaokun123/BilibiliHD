package com.duzhaokun123.bilibilihd.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.DynamicAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model.DynamicPage;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model.NestedCard;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DynamicFragment extends Fragment {

    private XRecyclerView mXrv;

    private Handler handler;
    private List<DynamicPage.Data.Card> mCards;

    private int page = 0;
    private long offsetDynamicId = 0;
    private SimpleDateFormat simpleDateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        SettingsManager settingsManager = SettingsManager.getSettingsManager();
        simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
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
                return new DynamicCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_dynamic_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Glide.with(mXrv).load(mCards.get(position).getDesc().getUser_profile().getInfo().getFace()).into(((DynamicCardHolder) holder).mCivFace);
                ((DynamicCardHolder) holder).mTvName.setText(mCards.get(position).getDesc().getUser_profile().getInfo().getUname());
                ((DynamicCardHolder) holder).mTvTime.setText(simpleDateFormat.format(new Date(mCards.get(position).getDesc().getTimestamp() * 1000)));
//                ((DynamicCardHolder) holder).mTvTime.setText(String.valueOf(mCards.get(position).getDesc().getTimestamp()));
                if (mCards.get(position).getDesc().getUser_profile().getVip().getVipType() != 1) {//1: 不是 VIP, 2: 是 VIP
                    ((DynamicCardHolder) holder).mTvName.setTextColor(getContext().getColor(R.color.colorAccent));
                }

                OpenUserSpace openUserSpace = new OpenUserSpace(mCards.get(position).getDesc().getUid());
                ((DynamicCardHolder) holder).mCivFace.setOnClickListener(openUserSpace);
                ((DynamicCardHolder) holder).mTvName.setOnClickListener(openUserSpace);


                NestedCard nestedCard = DynamicAPI.getNestedCard(mCards.get(position).getCard());
                if (nestedCard.getItem() != null) {
                    if (nestedCard.getItem().getDescription() != null) {
                        ((DynamicCardHolder) holder).mTvContent.setText(nestedCard.getItem().getDescription());
                    } else if (nestedCard.getItem().getContent() != null) {
                        ((DynamicCardHolder) holder).mTvContent.setText(nestedCard.getItem().getContent());
                    } else if (nestedCard.getItem().getDynamic() != null) {
                        ((DynamicCardHolder) holder).mTvContent.setText(nestedCard.getItem().getDynamic());
                    }
                }
                handleNestedCard(nestedCard, ((DynamicCardHolder) holder).mLl);

//                    ((DynamicCardHolder) holder).mTvContent.setText(mCards.get(position).getCard());

            }

            @Override
            public int getItemCount() {
                if (mCards == null) {
                    return 0;
                } else {
                    return mCards.size();
                }
            }

            class DynamicCardHolder extends RecyclerView.ViewHolder {

                private CircleImageView mCivFace;
                private TextView mTvName, mTvTime, mTvContent;
                private LinearLayout mLl;

                DynamicCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mCivFace = itemView.findViewById(R.id.cli_face);
                    mTvName = itemView.findViewById(R.id.tv_name);
                    mTvTime = itemView.findViewById(R.id.tv_time);
                    mTvContent = itemView.findViewById(R.id.tv_content);
                    mLl = itemView.findViewById(R.id.ll);
                }
            }

            class OpenUserSpace implements View.OnClickListener {

                private long uid;

                OpenUserSpace(long uid) {
                    this.uid = uid;
                }

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UserSpaceActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            }
        });
        mXrv.setLoadingMoreEnabled(true);
        mXrv.setPullRefreshEnabled(true);
        mXrv.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
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
        new Refresh().start();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    mXrv.refreshComplete();
                    XRecyclerViewUtil.notifyItemsChanged(mXrv, mCards.size() - 1);
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
            page = 0;
            DynamicAPI.getDynamicAPI().getDynamic(page, new MyBilibiliClient.Callback<DynamicPage>() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    ToastUtil.sendMsg(getContext(), e.getMessage());
                    Looper.loop();
                }

                @Override
                public void onSuccess(DynamicPage dynamicPage) {
                    mCards = dynamicPage.getData().getCards();
                    offsetDynamicId = dynamicPage.getData().getHistory_offset();
                    LoadMore loadMore = new LoadMore();
                    for (int i = 0; i <  2; i++) {
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
            page++;
            DynamicAPI.getDynamicAPI().getDynamic(page, String.valueOf(offsetDynamicId), new MyBilibiliClient.Callback<DynamicPage>() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    ToastUtil.sendMsg(getContext(), e.getMessage());
                    Looper.loop();
                }

                @Override
                public void onSuccess(DynamicPage dynamicPage) {
                    mCards.addAll(dynamicPage.getData().getCards());
                    offsetDynamicId = dynamicPage.getData().getHistory_offset();
                    handler.sendEmptyMessage(1);
                }
            });
        }
    }

    private void handleNestedCard(NestedCard nestedCard, ViewGroup viewGroup) {
        if (nestedCard.getItem() != null && nestedCard.getItem().getPictures() != null) {
            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_xrecyclerview_only, viewGroup);
            XRecyclerView xRecyclerView = view1.findViewById(R.id.xrv);
            xRecyclerView.setPullRefreshEnabled(false);
            xRecyclerView.setLoadingMoreEnabled(false);

            int spanCount = 1;
            switch (nestedCard.getItem().getPictures().size()) {
                case 1:
                    break;
                case 2:
                case 4:
                    spanCount = 2;
                    break;
                default:
                    spanCount = 3;
                    break;
            }

            xRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
            if (spanCount == 1) {
                xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
                    }
                });
            } else {
                xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.set(0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height), getResources().getDimensionPixelOffset(R.dimen.divider_height));
                    }
                });
            }
            xRecyclerView.setAdapter(new RecyclerView.Adapter() {

                private NestedCard card = nestedCard;

                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new PictureHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_imageview_only_item, parent, false));
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    Glide.with(getContext()).load(card.getItem().getPictures().get(position).getImg_src()).into(((PictureHolder) holder).mIv);
                    ((PictureHolder) holder).mIv.setOnClickListener(new View.OnClickListener() {

                        private String url = card.getItem().getPictures().get(position).getImg_src();

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public int getItemCount() {
                    if (card != null) {
                        return card.getItem().getPictures().size();
                    } else {
                        return 0;
                    }
                }

                class PictureHolder extends RecyclerView.ViewHolder {

                    private ImageView mIv;

                    PictureHolder(@NonNull View itemView) {
                        super(itemView);
                        mIv = itemView.findViewById(R.id.iv);
                    }
                }
            });
        }
        if (nestedCard.getAid() != 0) {
            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_video, viewGroup);
            if (nestedCard.getTitle() != null) {
                ((TextView)view1.findViewById(R.id.tv_title)).setText(nestedCard.getTitle());
            } else if (nestedCard.getItem() != null && nestedCard.getItem().getDynamic() != null) {
                ((TextView)view1.findViewById(R.id.tv_title)).setText(nestedCard.getItem().getDynamic());
            }
            ImageView imageView1 = view1.findViewById(R.id.iv);
            Glide.with(getContext()).load(nestedCard.getPic()).into(imageView1);
            RelativeLayout relativeLayout = view1.findViewById(R.id.rl);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 20-3-5
                }
            });
            relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {

//                private long aid = homePage.getData().getItems().get(position).getParam();

                @Override
                public boolean onLongClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(getContext(), relativeLayout);
                    popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.check_cover:
                                    Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                                    intent.putExtra("url", nestedCard.getPic());
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
            // TODO: 20-3-5
        }
        if (nestedCard.getOrigin() != null) {
            NestedCard nestedNestedCard = DynamicAPI.getNestedCard(nestedCard.getOrigin());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_linearlayout_only, viewGroup);
            LinearLayout linearLayout = view.findViewById(R.id.ll);
            linearLayout.setBackgroundResource(R.color.lightGray);
            handleNestedCard(nestedNestedCard, linearLayout);
        }
    }
}
