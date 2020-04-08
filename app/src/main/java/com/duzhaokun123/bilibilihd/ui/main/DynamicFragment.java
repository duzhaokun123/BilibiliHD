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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.DynamicAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model.DynamicPage;
import com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model.NestedCard;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.ui.widget.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.SimpleDateFormatUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DynamicFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> {

    private List<DynamicPage.Data.Card> mCards;

    private int page = 0;
    private long offsetDynamicId = 0;

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
                return new DynamicCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_dynamic_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((DynamicCardHolder) holder).mLl.removeAllViews();

                ((DynamicCardHolder) holder).mTvTime.setText(SimpleDateFormatUtil.getFormat1().format(mCards.get(position).getDesc().getTimestamp() * 1000));
                if (mCards.get(position).getDesc().getUser_profile() != null) {
                    GlideUtil.loadUrlInto(getContext(), mCards.get(position).getDesc().getUser_profile().getInfo().getFace(), ((DynamicCardHolder) holder).mCivFace, false);
                    ((DynamicCardHolder) holder).mTvName.setText(mCards.get(position).getDesc().getUser_profile().getInfo().getUname());
                    if (mCards.get(position).getDesc().getUser_profile().getVip().getVipType() != 1 && getContext() != null) {//1: 不是 VIP, 2: 是 VIP 吗?
                        ((DynamicCardHolder) holder).mTvName.setTextColor(getContext().getColor(R.color.colorAccent));
                    }
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
                } else if (nestedCard.getDynamic() != null && !nestedCard.getDynamic().equals("")) {
                    ((DynamicCardHolder) holder).mTvContent.setText(nestedCard.getDynamic());
                }
                handleNestedCard(nestedCard, ((DynamicCardHolder) holder).mLl, 1);

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
                    mCivFace = itemView.findViewById(R.id.civ_face);
                    mTvName = itemView.findViewById(R.id.tv_name);
                    mTvTime = itemView.findViewById(R.id.tv_time);
                    mTvContent = itemView.findViewById(R.id.tv_content);
                    mLl = itemView.findViewById(R.id.ll);
                }
            }
        });
        baseBind.xrv.setLoadingMoreEnabled(true);
        baseBind.xrv.setPullRefreshEnabled(true);
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
        baseBind.xrv.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    }

    @Override
    protected void initData() {
        baseBind.xrv.refresh();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                baseBind.xrv.refreshComplete();
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, mCards.size() - 1);
                break;
            case 1:
                baseBind.xrv.loadMoreComplete();
                break;
            case 2:
                baseBind.xrv.loadMoreComplete();
                baseBind.xrv.refreshComplete();
        }
    }

    class Refresh extends Thread {
        @Override
        public void run() {
            page = 0;
            DynamicAPI.getInstance().getDynamicNew(new MyBilibiliClient.ICallback<DynamicPage>() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    if (handler != null) {
                        handler.sendEmptyMessage(2);
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> ToastUtil.sendMsg(getContext(), e.getMessage()));
                    }
                }

                @Override
                public void onSuccess(DynamicPage dynamicPage) {
                    mCards = dynamicPage.getData().getCards();
                    offsetDynamicId = dynamicPage.getData().getHistory_offset();
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
            page++;
            DynamicAPI.getInstance().getDynamicHistory(page, offsetDynamicId, new MyBilibiliClient.ICallback<DynamicPage>() {
                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    if (handler != null) {
                        handler.sendEmptyMessage(2);
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> ToastUtil.sendMsg(getContext(), e.getMessage()));
                    }
                }

                @Override
                public void onSuccess(DynamicPage dynamicPage) {
                    mCards.addAll(dynamicPage.getData().getCards());
                    offsetDynamicId = dynamicPage.getData().getNext_offset();
                    if (handler != null) {
                        handler.sendEmptyMessage(1);
                    }
                }
            });
        }
    }

    private void handleNestedCard(NestedCard nestedCard, ViewGroup viewGroup, int depth) {
        if (nestedCard.getItem() != null && nestedCard.getItem().getPictures() != null) {
            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dynamic_card_item, viewGroup);
            View view2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_xrecyclerview_only, view1.findViewById(R.id.ll));

            CardView cardView = view1.findViewById(R.id.cv);
            RelativeLayout relativeLayout = view1.findViewById(R.id.rl);
            TextView tvContent = view1.findViewById(R.id.tv_content);
            if (depth == 1) {
                relativeLayout.removeView(tvContent);
            } else {
                tvContent.setText(nestedCard.getItem().getDescription());
            }

            cardView.setBackgroundResource(android.R.color.transparent);
            GlideUtil.loadUrlInto(getContext(), nestedCard.getUser().getHead_url(), view1.findViewById(R.id.civ_face), false);
            ((TextView) view1.findViewById(R.id.tv_name)).setText(nestedCard.getUser().getName());
            view1.findViewById(R.id.civ_face).setOnClickListener(new OpenUserSpace(nestedCard.getUser().getUid()));

            XRecyclerView xRecyclerView = view2.findViewById(R.id.xrv);
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
                    GlideUtil.loadUrlInto(getContext(), card.getItem().getPictures().get(position).getImg_src(), ((PictureHolder) holder).mIv, true);
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
                ((TextView) view1.findViewById(R.id.tv_title)).setText(nestedCard.getTitle());
            } else if (nestedCard.getItem() != null && nestedCard.getItem().getDynamic() != null) {
                ((TextView) view1.findViewById(R.id.tv_title)).setText(nestedCard.getItem().getDynamic());
            }
            ImageView imageView1 = view1.findViewById(R.id.iv);
            GlideUtil.loadUrlInto(getContext(), nestedCard.getPic(), imageView1, false);
            RelativeLayout relativeLayout = view1.findViewById(R.id.rl);
            ((TextView) relativeLayout.findViewById(R.id.tv_up)).setText(nestedCard.getOwner().getName());
            GlideUtil.loadUrlInto(getContext(), nestedCard.getOwner().getFace(), relativeLayout.findViewById(R.id.civ_face), false);
            relativeLayout.findViewById(R.id.civ_face).setOnClickListener(new OpenUserSpace(nestedCard.getOwner().getMid()));
            relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {

                private long aid = MyBilibiliClientUtil.getAidFromBilibiliLink(nestedCard.getJump_url());

                @Override
                public boolean onLongClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(getContext(), relativeLayout);
                    popupMenu.getMenuInflater().inflate(R.menu.video_card, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(item -> {
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
                    });
                    popupMenu.show();
                    return true;
                }
            });
            relativeLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), PlayActivity.class);
                intent.putExtra("aid", MyBilibiliClientUtil.getAidFromBilibiliLink(nestedCard.getJump_url()));
                startActivity(intent);
            });
        }
        if (nestedCard.getOrigin() != null) {
            NestedCard nestedNestedCard = DynamicAPI.getNestedCard(nestedCard.getOrigin());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_linearlayout_only, viewGroup);
            LinearLayout linearLayout = view.findViewById(R.id.ll);
            linearLayout.setBackgroundResource(R.color.lightGray);
            handleNestedCard(nestedNestedCard, linearLayout, depth + 1);
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
}
