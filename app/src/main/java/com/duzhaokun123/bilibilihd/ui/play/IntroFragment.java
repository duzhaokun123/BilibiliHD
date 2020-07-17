package com.duzhaokun123.bilibilihd.ui.play;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.FragmentPlayIntroBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.model.Base;
import com.duzhaokun123.bilibilihd.mybilibiliapi.toview.ToViewAPI;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.SimpleDateFormatUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class IntroFragment extends BaseFragment<FragmentPlayIntroBinding> {
    public static final int WHAT_LOAD_NEW_PAGE = 2;

    private TextView mTvUpName, mTvUpFans;
    private CircleImageView mCivFace;

    private com.hiczp.bilibili.api.app.model.View biliView;
    private VideoPlayUrl videoPlayUrl;
    private long aid;
    private int page;

    public static IntroFragment getInstance(com.hiczp.bilibili.api.app.model.View biliView, long aid, int page) {
        IntroFragment introFragment = new IntroFragment();
        introFragment.biliView = biliView;
        introFragment.aid = aid;
        introFragment.page = page;
        return introFragment;
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_play_intro;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        biliView = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("biliView"), com.hiczp.bilibili.api.app.model.View.class);
        videoPlayUrl = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("videoplayUrl"), VideoPlayUrl.class);
        aid = savedInstanceState.getLong("aid");
        page = savedInstanceState.getInt("page");
    }

    @Override
    protected void findViews(View parentView) {
        mTvUpName = parentView.findViewById(R.id.tv_name);
        mTvUpFans = parentView.findViewById(R.id.tv_content);
        mCivFace = parentView.findViewById(R.id.civ_face);
    }

    @Override
    protected void initView() {
        baseBind.tvId.setText(MyBilibiliClientUtil.av2bv(aid));

        BaseActivity<?> baseActivity = getBaseActivity();
        if (baseActivity != null) {
            ViewGroup.LayoutParams params = baseBind.v.getLayoutParams();
            params.height = baseActivity.getFixButtonHeight();
            baseBind.v.setLayoutParams(params);
        }
    }

    @Override
    protected void initData() {
        new LoadVideoPlayUrl(biliView.getData().getPages().get(page - 1).getCid(), 0).start();
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                sendBack();
                GlideUtil.loadUrlInto(getContext(), biliView.getData().getOwner().getFace(), mCivFace, false);
                mCivFace.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), UserSpaceActivity.class);
                    intent.putExtra("uid", biliView.getData().getOwner().getMid());
                    startActivity(intent);
                });
                mTvUpName.setText(biliView.getData().getOwner().getName());
                mTvUpName.setOnClickListener(v -> mCivFace.callOnClick());
                mTvUpFans.setText(getString(R.string.num_fans, biliView.getData().getOwnerExt().getFans()));
                baseBind.tvDesc.setText(biliView.getData().getDesc());
                baseBind.tvUptime.setText(SimpleDateFormatUtil.getFormat1().format(biliView.getData().getPubdate() * 1000L));
                baseBind.tvDanmakuHas.setText(String.valueOf(biliView.getData().getStat().getDanmaku()));
                baseBind.tvWatched.setText(String.valueOf(biliView.getData().getStat().getView()));
                baseBind.tvTitle.setText(biliView.getData().getTitle());
                for (com.hiczp.bilibili.api.app.model.View.Data.Page page1 : biliView.getData().getPages()) {
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setText(page1.getPart());
                    radioButton.setOnClickListener(v -> {
                        if (page != page1.getPage()) {
                            new LoadVideoPlayUrl(page1.getCid(), 1).start();
                            page = page1.getPage();
                        }
                    });
                    radioButton.setButtonDrawable(null);
                    radioButton.setBackgroundResource(R.drawable.rb_video_page_bg);
                    radioButton.setTextColor(requireContext().getColorStateList(R.color.rb_video_page_text));
                    radioButton.setPadding(OtherUtils.dp2px(10),
                            OtherUtils.dp2px(10),
                            OtherUtils.dp2px(10),
                            OtherUtils.dp2px(10));
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.rightMargin = OtherUtils.dp2px(5);
                    baseBind.rgPages.addView(radioButton, params);
                    if (page == page1.getPage()) {
                        radioButton.setChecked(true);
                    }
                }
                baseBind.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
                    }
                });
                baseBind.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                baseBind.rv.setAdapter(new Adapter());
                break;
            case 1:
                sendBack();
                break;
            case WHAT_LOAD_NEW_PAGE:
                new LoadVideoPlayUrl(biliView.getData().getPages().get(msg.arg1 - 1).getCid(), 1).start();
                ((RadioButton) baseBind.rgPages.getChildAt(page - 1)).setChecked(false);
                page = msg.arg1;
                ((RadioButton) baseBind.rgPages.getChildAt(page - 1)).setChecked(true);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("biliView", GsonUtil.getGsonInstance().toJson(biliView));
        outState.putString("videoPlayUrl", GsonUtil.getGsonInstance().toJson(videoPlayUrl));
        outState.putLong("aid", aid);
        outState.putInt("page", page);
    }

    private void sendBack() {
        Message message = new Message();
        message.what = PlayActivity.WHAT_INTRO_FRAGMENT_SEND_BACK;
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putString("videoPlayUrl", GsonUtil.getGsonInstance().toJson(videoPlayUrl));
        message.setData(bundle);
        Objects.requireNonNull(Objects.requireNonNull(getBaseActivity()).getHandler()).sendMessage(message);
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RelateVideoCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_relate_video_card_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((RelateVideoCardHolder) holder).mTvTitle.setText(biliView.getData().getRelates().get(position).getTitle());
            ((RelateVideoCardHolder) holder).mTvPlay.setText(String.valueOf(biliView.getData().getRelates().get(position).getStat().getReply()));
            ((RelateVideoCardHolder) holder).mTvDanmaku.setText(String.valueOf(biliView.getData().getRelates().get(position).getStat().getDanmaku()));
            GlideUtil.loadUrlInto(getContext(), biliView.getData().getRelates().get(position).getPic(), ((RelateVideoCardHolder) holder).mIvCover, false);
            if (biliView.getData().getRelates().get(position).getOwner() != null) {
                ((RelateVideoCardHolder) holder).mtvUp.setText(biliView.getData().getRelates().get(position).getOwner().getName());
            }
            ((RelateVideoCardHolder) holder).mCv.setOnClickListener(v -> {
                if (biliView.getData().getRelates().get(position).getAid() != 0) {
                    Intent intent = new Intent(getContext(), PlayActivity.class);
                    intent.putExtra("aid", (long) biliView.getData().getRelates().get(position).getAid());
                    startActivity(intent);
                } else {
                    BrowserUtil.openCustomTab(requireContext(), biliView.getData().getRelates().get(position).getUri());
                }
            });
            ((RelateVideoCardHolder) holder).mCv.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(requireContext(), ((RelateVideoCardHolder) holder).mCv);
                popupMenu.inflate(R.menu.video_card);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.check_cover:
                            Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                            intent.putExtra("url", biliView.getData().getRelates().get(position).getPic());
                            startActivity(intent);
                            break;
                        case R.id.add_to_watch_later:
                            new Thread() {
                                @Override
                                public void run() {
                                    ToViewAPI.getInstance().addAid(biliView.getData().getRelates().get(position).getAid(), new MyBilibiliClient.ICallback<Base>() {
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
            });
        }

        @Override
        public int getItemCount() {
            if (biliView == null || biliView.getData().getRelates() == null) {
                return 0;
            } else {
                return biliView.getData().getRelates().size();
            }
        }

        class RelateVideoCardHolder extends RecyclerView.ViewHolder {
            private ImageView mIvCover;
            private TextView mTvTitle, mtvUp, mTvPlay, mTvDanmaku;
            private CardView mCv;

            RelateVideoCardHolder(@NonNull View itemView) {
                super(itemView);
                mIvCover = itemView.findViewById(R.id.iv_cover);
                mTvTitle = itemView.findViewById(R.id.tv_title);
                mCv = itemView.findViewById(R.id.cv);
                mtvUp = itemView.findViewById(R.id.tv_up);
                mTvPlay = itemView.findViewById(R.id.tv_play);
                mTvDanmaku = itemView.findViewById(R.id.tv_danmaku);
            }
        }
    }

    class LoadVideoPlayUrl extends Thread {
        long cid;
        int what;

        LoadVideoPlayUrl(long cid, int what) {
            this.cid = cid;
            this.what = what;
        }

        @Override
        public void run() {
            try {
                videoPlayUrl = PBilibiliClient.Companion.getInstance().getPPlayerAPI().videoPlayUrl(aid, cid);
                if (handler != null) {
                    handler.sendEmptyMessage(what);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> TipUtil.showToast(e.getMessage()));
                }
            }
        }
    }
}
