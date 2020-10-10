package com.duzhaokun123.bilibilihd.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.FragmentSettingsUsersBinding;
import com.duzhaokun123.bilibilihd.ui.login.LoginActivity;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.LoginUserInfoMap;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.hiczp.bilibili.api.app.model.MyInfo;
import com.hiczp.bilibili.api.app.model.Space;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsUsersFragment extends BaseFragment<FragmentSettingsUsersBinding> {

    private LoginUserInfoMap loginUserInfoMap;
    private MyInfo myInfo;
    private LoginResponse exportLoginResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginUserInfoMap = Settings.getLoginUserInfoMap();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_settings_users;
    }

    @Override
    protected void initView() {
        baseBind.xrv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        baseBind.xrv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
            }
        });
        baseBind.xrv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UserCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_user_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((UserCardHolder) holder).mTvContent.setText(String.valueOf(Objects.requireNonNull(loginUserInfoMap.getByIndex(position)).getUserId()));
                ((UserCardHolder) holder).mCv.setOnClickListener(v -> {
                    Application.getPBilibiliClient().setLoginResponse(loginUserInfoMap.getByIndex(position));
                    loginUserInfoMap.setLoggedUid(Objects.requireNonNull(loginUserInfoMap.getByIndex(position)).getUserId());
                    Settings.saveLoginUserInfoMap();
                    reloadLoggedUserInfo();
                });
                ((UserCardHolder) holder).mCv.setOnLongClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(requireContext(), ((UserCardHolder) holder).mCv);
                    popupMenu.getMenuInflater().inflate(R.menu.settings_user, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.export:
                                exportLoginResponse = loginUserInfoMap.getByIndex(position);
                                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("application/json");
                                intent.putExtra(Intent.EXTRA_TITLE, "loginResponse.json");
                                startActivityForResult(intent, 1);
                                break;
                            case R.id.delete:
                                new AlertDialog.Builder(requireContext()).setIcon(R.drawable.ic_info)
                                        .setTitle(R.string.delete)
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            loginUserInfoMap.remove(Objects.requireNonNull(loginUserInfoMap.getByIndex(position)).getUserId());
                                            XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, loginUserInfoMap.size());
                                            Settings.saveLoginUserInfoMap();
                                            Application.getPBilibiliClient().setLoginResponse(loginUserInfoMap.getLoggedLoginResponse());
                                            reloadLoggedUserInfo();
                                        })
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .show();
                                break;
                        }
                        return true;
                    });
                    popupMenu.show();
                    return true;
                });

                new Thread(() -> {
                    try {
                        Space space = Application.getPBilibiliClient().getPAppAPI().space(Objects.requireNonNull(loginUserInfoMap.getByIndex(position)).getUserId());
                        Application.runOnUiThread(() -> {
                            if (getContext() != null) {
                                GlideUtil.loadUrlInto(getContext(), space.getData().getCard().getFace(), ((UserCardHolder) holder).mCivFace, false);
                            }
                            ((UserCardHolder) holder).mTvName.setText(space.getData().getCard().getName());
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Application.runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                    }
                }).start();

            }

            @Override
            public int getItemCount() {
                if (loginUserInfoMap != null) {
                    return loginUserInfoMap.size();
                } else {
                    return 0;
                }
            }

            class UserCardHolder extends RecyclerView.ViewHolder {

                private CircleImageView mCivFace;
                private TextView mTvName, mTvContent;
                private CardView mCv;

                UserCardHolder(@NonNull View itemView) {
                    super(itemView);
                    mCivFace = itemView.findViewById(R.id.civ_face);
                    mTvName = itemView.findViewById(R.id.tv_name);
                    mTvContent = itemView.findViewById(R.id.tv_content);
                    mCv = itemView.findViewById(R.id.cv);
                }
            }
        });
        baseBind.xrv.setLoadingMoreEnabled(false);
        baseBind.xrv.setPullRefreshEnabled(false);

        baseBind.ibDelete.setOnClickListener(v -> {
            loginUserInfoMap.setLoggedUid(0);
            Settings.saveLoginUserInfoMap();
            Application.getPBilibiliClient().setLoginResponse(null);
            reloadLoggedUserInfo();
            BrowserUtil.syncLoggedLoginResponse();
        });

        baseBind.ibAdd.setOnClickListener(v -> new AlertDialog.Builder(requireContext()).setTitle(R.string.add)
                .setIcon(R.drawable.ic_add_circle)
                .setItems(new String[]{
                        getString(R.string.login),
                        getString(R.string.import_)
                }, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                            intent1.setType("*/*");
                            intent1.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent1, 0);
                            break;
                    }
                })
                .show());

    }

    @Override
    protected void initData() {
        reloadLoggedUserInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null && resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    LoginResponse loginResponse = OtherUtils.readLoginResponseFromUri(getContext(), uri);
                    if (loginResponse != null) {
                        loginUserInfoMap.put(loginResponse.getUserId(), loginResponse);
                        Settings.saveLoginUserInfoMap();
                        XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, loginUserInfoMap.size());
                        TipUtil.showTip(getContext(), R.string.imported);
                    } else {
                        TipUtil.showTip(getContext(), R.string.bad_file);
                    }
                }
                break;
            case 1:
                if (data != null) {
                    Uri uri = data.getData();
                    if (!OtherUtils.writeLoginResponseToUri(getContext(), exportLoginResponse, uri)) {
                        TipUtil.showTip(getContext(), R.string.failure);
                    } else {
                        TipUtil.showTip(getContext(), R.string.exported);
                    }
                    exportLoginResponse = null;
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadLoggedUserInfo();
    }

    private void reloadLoggedUserInfo() {
        if (loginUserInfoMap.getLoggedUdi() != 0) {
            LoginResponse loginResponse = loginUserInfoMap.getLoggedLoginResponse();
            if (loginResponse != null) {
                baseBind.tvContent.setText(String.valueOf(loginResponse.getUserId()));
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            myInfo = Application.getPBilibiliClient().getPAppAPI().getMyInfo();
                            if (handler != null) {
                                handler.sendEmptyMessage(2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (getContext() != null) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                                }
                            }
                        }
                    }
                }.start();
            }
        } else {
            baseBind.tvContent.setText(null);
            baseBind.tvName.setText(R.string.not_logged_in);
            baseBind.civFace.setImageDrawable(null);
        }
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
            case 1:
                msg.getCallback().run();
                break;
            case 2:
                baseBind.tvName.setText(myInfo.getData().getName());
                if (getContext() != null) {
                    Glide.with(getContext()).load(myInfo.getData().getFace()).into(baseBind.civFace);
                }
                break;
        }
    }
}
