package com.duzhaokun123.bilibilihd.ui.play.online;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.FragmentPlayIntroBinding;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.LinkifyUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.DateTimeFormatUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.hiczp.bilibili.api.app.model.FavoritePage;
import com.hiczp.bilibili.api.app.model.LikeResponse;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;

import java.util.Objects;

import bilibili.app.view.v1.ViewV1;
import de.hdodenhof.circleimageview.CircleImageView;

public class IntroFragment extends BaseFragment<FragmentPlayIntroBinding> {
    public static final int WHAT_LOAD_NEW_PAGE = 2;

    private TextView mTvUpName, mTvUpFans;
    private CircleImageView mCivFace;

    private ViewV1.ViewReply biliView;
    private VideoPlayUrl videoPlayUrl;
    private long aid;
    private int page;

    public static IntroFragment getInstance(ViewV1.ViewReply biliView, long aid, int page) {
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
    protected void findViews(View parentView) {
        mTvUpName = parentView.findViewById(R.id.tv_name);
        mTvUpFans = parentView.findViewById(R.id.tv_content);
        mCivFace = parentView.findViewById(R.id.civ_face);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        baseBind.tvBvid.setText(MyBilibiliClientUtil.av2bv(aid));
        baseBind.tvAid.setText("av" + aid);

        BaseActivity<?> baseActivity = getBaseActivity();
        if (baseActivity != null) {
            ViewGroup.LayoutParams params = baseBind.v.getLayoutParams();
            params.height = baseActivity.getFixButtonHeight();
            baseBind.v.setLayoutParams(params);
        }
        for (ViewV1.Tag tag : biliView.getTagList()) {
            TextView textView = new TextView(getContext());
            textView.setText(tag.getName());
            textView.setTextSize(12);
            textView.setBackgroundResource(R.drawable.bg_tag);
            textView.setTextColor(getResources().getColor(R.color.ordinaryText, null));
            textView.setPadding(OtherUtils.dp2px(10), OtherUtils.dp2px(5), OtherUtils.dp2px(10), OtherUtils.dp2px(5));
            textView.setOnClickListener(v -> BrowserUtil.openWebViewActivity(requireContext(), "https://www.bilibili.com/v/channel/" + tag.getId(), true, true));
            baseBind.fblTags.addView(textView);
        }
        if (biliView.hasHonor()) {
            baseBind.llHonor.setVisibility(View.VISIBLE);
            baseBind.tvHonorText.setText(biliView.getHonor().getText());
            baseBind.tvHonorTextExtra.setText(biliView.getHonor().getTextExtra());
            baseBind.tvHonorUrl.setText(biliView.getHonor().getUrlText());
            int textColor;
            if (OtherUtils.isNightMode()) {
                Glide.with(this).load(biliView.getHonor().getIconNight()).into(baseBind.ivHonorIcon);
                baseBind.llHonor.setBackgroundColor(Color.parseColor(biliView.getHonor().getBgColorNight()));
                textColor = Color.parseColor(biliView.getHonor().getTextColorNight());
            } else {
                Glide.with(this).load(biliView.getHonor().getIcon()).into(baseBind.ivHonorIcon);
                baseBind.llHonor.setBackgroundColor(Color.parseColor(biliView.getHonor().getBgColor()));
                textColor = Color.parseColor(biliView.getHonor().getTextColor());
            }
            baseBind.tvHonorText.setTextColor(textColor);
            baseBind.tvHonorTextExtra.setTextColor(textColor);
            baseBind.tvHonorUrl.setTextColor(textColor);
            baseBind.llHonor.setOnClickListener(v -> BrowserUtil.openWebViewActivity(requireContext(), biliView.getHonor().getUrl(), false, true));
        }
        baseBind.tvLike.setOnClickListener(v -> new Thread(() -> {
            try {
                LikeResponse likeResponse = Application.getPBilibiliClient().getPAppAPI().like(aid, true);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        TipUtil.showTip(getContext(), likeResponse.getData().getToast());
                        baseBind.tvLike.setText(String.valueOf(biliView.getArc().getStat().getLike() + 1));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                }
            }
        }).start());
        baseBind.tvDislike.setOnClickListener(v -> new Thread(() -> {
            try {
                Application.getPBilibiliClient().getPAppAPI().dislike(aid, true);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), R.string.disliked));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                }
            }
        }).start());
        baseBind.tvCoin.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), baseBind.tvCoin);
            popupMenu.getMenu().add(0, 1, 1, "1");
            if (biliView.getArc().getCopyright() == 1) {
                popupMenu.getMenu().add(0, 2, 2, "2");
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                try {
                    Application.getPBilibiliClient().getPAppAPI().addCoin(aid, item.getOrder());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            TipUtil.showTip(getContext(), getString(R.string.added_coin_d, item.getOrder()));
                            baseBind.tvCoin.setText(String.valueOf(biliView.getArc().getStat().getCoin() + item.getOrder()));
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                    }
                }
                return true;
            });
            popupMenu.show();
        });
        baseBind.tvFavorite.setOnClickListener(v -> new Thread(() -> {
            try {
                FavoritePage favoritePage = Application.getPBilibiliClient().getPAppAPI().favoritePage(Application.getPBilibiliClient().getUid());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        PopupMenu popupMenu = new PopupMenu(requireContext(), baseBind.tvFavorite);
                        for (int i = 0; i < favoritePage.getData().getFavorite().getItems().size(); i++) {
                            popupMenu.getMenu().add(0, i, i, favoritePage.getData().getFavorite().getItems().get(i).getName());
                        }
                        popupMenu.setOnMenuItemClickListener(item -> {
                            new Thread(() -> {
                                try {
                                    Application.getPBilibiliClient().getPMainAPI().addFavoriteVideo(aid, favoritePage.getData().getFavorite().getItems().get(item.getOrder()).getFid());
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), getString(R.string.favorited_s, favoritePage.getData().getFavorite().getItems().get(item.getOrder()).getName())));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                                    }
                                }
                            }).start();
                            return true;
                        });
                        popupMenu.show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                }
            }
        }).start());
        if (biliView.getArc().getCopyright() == 1) {
            baseBind.tvSelfMade.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        GlideUtil.loadUrlInto(getContext(), biliView.getArc().getAuthor().getFace(), mCivFace, false);
        mCivFace.setOnClickListener(v -> UserSpaceActivity.enter(getActivity(), biliView.getArc().getAuthor().getMid(), mCivFace, null));
        mTvUpName.setText(biliView.getArc().getAuthor().getName());
        mTvUpName.setOnClickListener(v -> mCivFace.callOnClick());
        mTvUpFans.setText(getString(R.string.num_fans, biliView.getOwnerExt().getFans()));
        baseBind.tvDesc.setText(biliView.getArc().getDesc());
        LinkifyUtil.INSTANCE.addAllLinks(baseBind.tvDesc);
        baseBind.tvUptime.setText(DateTimeFormatUtil.getFormat1().format(biliView.getArc().getPubdate() * 1000L));
        baseBind.tvDanmakuHas.setText(String.valueOf(biliView.getArc().getStat().getDanmaku()));
        baseBind.tvWatched.setText(String.valueOf(biliView.getArc().getStat().getView()));
        baseBind.tvTitle.setText(biliView.getArc().getTitle());
        baseBind.tvLike.setText(String.valueOf(biliView.getArc().getStat().getLike()));
        baseBind.tvCoin.setText(String.valueOf(biliView.getArc().getStat().getCoin()));
        baseBind.tvFavorite.setText(String.valueOf(biliView.getArc().getStat().getFav()));
        new LoadVideoPlayUrl(biliView.getPagesList().get(page - 1).getPage().getCid(), 0).start();
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                sendBack();
                for (ViewV1.ViewPage page1 : biliView.getPagesList()) {
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setText(page1.getPage().getPart());
                    radioButton.setOnClickListener(v -> {
                        if (page != page1.getPage().getPage()) {
                            new LoadVideoPlayUrl(page1.getPage().getCid(), 1).start();
                            page = page1.getPage().getPage();
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
                    if (page == page1.getPage().getPage()) {
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
                baseBind.rv.setAdapter(new RelatesAdapter(requireContext(), biliView));
                break;
            case 1:
                sendBack();
                break;
            case WHAT_LOAD_NEW_PAGE:
                new LoadVideoPlayUrl(biliView.getPagesList().get(msg.arg1 - 1).getPage().getCid(), 1).start();
                ((RadioButton) baseBind.rgPages.getChildAt(page - 1)).setChecked(false);
                page = msg.arg1;
                ((RadioButton) baseBind.rgPages.getChildAt(page - 1)).setChecked(true);
                break;
        }
    }

    private void sendBack() {
        Message message = new Message();
        message.what = OnlinePlayActivity.WHAT_INTRO_FRAGMENT_SEND_BACK;
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putString("videoPlayUrl", GsonUtil.getGsonInstance().toJson(videoPlayUrl));
        message.setData(bundle);
        Objects.requireNonNull(Objects.requireNonNull(getBaseActivity()).getHandler()).sendMessage(message);
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
                videoPlayUrl = Application.getPBilibiliClient().getPPlayerAPI().videoPlayUrl(aid, cid);
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
