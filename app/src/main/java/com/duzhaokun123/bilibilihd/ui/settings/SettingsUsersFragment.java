package com.duzhaokun123.bilibilihd.ui.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.SpaceAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.space.model.Space;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.LoginActivity;
import com.duzhaokun123.bilibilihd.utils.LoginUserInfoMap;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.hiczp.bilibili.api.app.model.MyInfo;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsUsersFragment extends Fragment {

    private XRecyclerView mXrv;
    private CircleImageView mCivFace;
    private TextView mTvName, mTvContent;
    private ImageButton mIbDelete, mIbAdd;

    private LoginUserInfoMap loginUserInfoMap;
    private SettingsManager settingsManager;
    private Handler handler;
    private MyInfo myInfo;
    private LoginResponse exportLoginResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginUserInfoMap = SettingsManager.getSettingsManager().getLoginUserInfoMap(getContext());
        settingsManager = SettingsManager.getSettingsManager();
        handler = new Handler();
        return inflater.inflate(R.layout.fragment_settings_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mXrv = view.findViewById(R.id.xrv);
        mCivFace = view.findViewById(R.id.civ_face);
        mTvName = view.findViewById(R.id.tv_name);
        mTvContent = view.findViewById(R.id.tv_content);
        mIbDelete = view.findViewById(R.id.ib_delete);
        mIbAdd = view.findViewById(R.id.ib_add);

        mXrv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mXrv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
            }
        });
        mXrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UserCardHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_user_card_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((UserCardHolder) holder).mTvContent.setText(String.valueOf(loginUserInfoMap.getByIndex(position).getUserId()));
                ((UserCardHolder) holder).mCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().setLoginResponse(loginUserInfoMap.getByIndex(position));
                        loginUserInfoMap.setLoggedUid(loginUserInfoMap.getByIndex(position).getUserId());
                        settingsManager.saveLoginUserInfoMap(getContext());
                        reloadLoggedUserInfo();
                    }
                });
                ((UserCardHolder) holder).mCv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getContext(), ((UserCardHolder) holder).mCv);
                        popupMenu.getMenuInflater().inflate(R.menu.settings_user, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.export:
                                        exportLoginResponse = loginUserInfoMap.getByIndex(position);
                                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.setType("*/*");
                                        intent.putExtra(Intent.EXTRA_TITLE, "loginResponse");
                                        startActivityForResult(intent, 1);
                                        break;
                                    case R.id.delete:
                                        new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_info)
                                                .setTitle(R.string.delete)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        loginUserInfoMap.remove(loginUserInfoMap.getByIndex(position).getUserId());
                                                        XRecyclerViewUtil.notifyItemsChanged(mXrv, loginUserInfoMap.size());
                                                        settingsManager.saveLoginUserInfoMap(getContext());
                                                        reloadLoggedUserInfo();
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.cancel, null)
                                                .show();
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                });
                new Thread() {

                    private long uid = loginUserInfoMap.getByIndex(position).getUserId();

                    @Override
                    public void run() {
                        SpaceAPI.getSpaceAPI().getSpace(uid, new MyBilibiliClient.Callback<Space>() {
                            @Override
                            public void onException(Exception e) {
                                e.printStackTrace();
                                Looper.prepare();
                                ToastUtil.sendMsg(getContext(), e.getMessage());
                                Looper.loop();
                            }

                            @Override
                            public void onSuccess(Space space) {
                                Message message = Message.obtain(null, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (getContext() != null) {
                                            Glide.with(getContext()).load(space.getData().getCard().getFace()).into(((UserCardHolder) holder).mCivFace);
                                        }
                                        ((UserCardHolder) holder).mTvName.setText(space.getData().getCard().getName());
                                    }
                                });
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        });
                    }
                }.start();

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
        mXrv.setLoadingMoreEnabled(false);
        mXrv.setPullRefreshEnabled(false);

        mIbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserInfoMap.setLoggedUid(0);
                settingsManager.saveLoginUserInfoMap(getContext());
                reloadLoggedUserInfo();
            }
        });

        mIbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle(R.string.add)
                        .setIcon(R.drawable.ic_add_circle)
                        .setItems(new String[]{
                                getString(R.string.login),
                                getString(R.string.import_)
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                            }
                        })
                        .show();
            }
        });

        reloadLoggedUserInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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
                        settingsManager.saveLoginUserInfoMap(getContext());
                        XRecyclerViewUtil.notifyItemsChanged(mXrv, loginUserInfoMap.size());
                    } else {
                        ToastUtil.sendMsg(getContext(), R.string.bad_file);
                    }
                }
                break;
            case 1:
                if (data != null) {
                    Uri uri = data.getData();
                    if (!OtherUtils.writeLoginResponseToUri(getContext(), exportLoginResponse, uri)) {
                        ToastUtil.sendMsg(getContext(), R.string.failure);
                    }
                    exportLoginResponse = null;
                }
                break;
        }
    }

    private void reloadLoggedUserInfo() {
        if (loginUserInfoMap.getLoggedUdi() != 0) {
            mTvContent.setText(String.valueOf(loginUserInfoMap.getLoggedLoginResponse().getUserId()));
            new Thread() {
                @Override
                public void run() {
                    try {
                        myInfo = PBilibiliClient.Companion.getPBilibiliClient().getPAppAPI().getMyInfo();
                        handler.sendEmptyMessage(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getContext() != null) {
                            Looper.prepare();
                            ToastUtil.sendMsg(getContext(), e.getMessage());
                            Looper.loop();
                        }
                    }
                }
            }.start();
        } else {
            mTvContent.setText(null);
            mTvName.setText(R.string.not_logged_in);
            mCivFace.setImageDrawable(null);
        }
    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                case 1:
                    msg.getCallback().run();
                    break;
                case 2:
                    mTvName.setText(myInfo.getData().getName());
                    if (getContext() != null) {
                        Glide.with(getContext()).load(myInfo.getData().getFace()).into(mCivFace);
                    }
                    break;
            }
        }
    }
}
