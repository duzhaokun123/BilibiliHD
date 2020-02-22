package com.duzhaokun123.bilibilihd.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.hiczp.bilibili.api.app.model.Space;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.duzhaokun123.bilibilihd.utils.OtherUtils.setLevelDrawable;
import static com.duzhaokun123.bilibilihd.utils.OtherUtils.setSixDrawable;

public class UserSpaceActivity extends AppCompatActivity {

    private ImageView mIvSpeaceImage, mIvSex,mIvLevel;
    private CircleImageView mCivFace;
    private TextView mTvName, mTvFans, mTvWatching, mTvLike, mTvSign;

    private Bundle bundle;
    private PBilibiliClient pBilibiliClient;
    private Space space;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_space);

        mIvSpeaceImage = findViewById(R.id.iv_spaceImage);
        mIvSex = findViewById(R.id.iv_sex);
        mIvLevel = findViewById(R.id.iv_level);
        mCivFace = findViewById(R.id.civ_face);
        mTvName = findViewById(R.id.tv_name);
        mTvFans = findViewById(R.id.tv_fans);
        mTvWatching = findViewById(R.id.tv_watching);
        mTvLike = findViewById(R.id.tv_like);
        mTvSign = findViewById(R.id.tv_sign);

        bundle = getIntent().getExtras();

        // FIXME: 20-2-22 你看下面黄的
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Glide.with(mCivFace).load(space.getData().getCard().getFace()).into(mCivFace);
                setSixDrawable(mIvSex, space.getData().getCard().getSex());
                setLevelDrawable(mIvLevel, space.getData().getCard().getLevelInfo().getCurrentLevel());
                mTvName.setText(space.getData().getCard().getName());
                mTvFans.setText(String.valueOf(space.getData().getCard().getFans()));
                mTvSign.setText(space.getData().getCard().getSign());
                mTvWatching.setText(String.valueOf(space.getData().getCard().getAttention()));
                // TODO: 20-2-22 试出 mTvLike 和 mIvSpaceImage 对应的键
            }
        };

        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    space = pBilibiliClient.getPAppAPI().space(bundle.getLong("uid"));
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    ToastUtil.sendMsg(UserSpaceActivity.this, e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
