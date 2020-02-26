package com.duzhaokun123.bilibilihd.ui.play;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;

public class PlayActivity extends AppCompatActivity {

    private TextView mTv;

    private PBilibiliClient pBilibiliClient;
    private VideoPlayUrl videoPlayUrl;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mTv = findViewById(R.id.tv);

        handler = new Handler();

        setTitle("av" + getIntent().getExtras().getString("av"));
        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();


        new Thread(new Runnable() {
            @Override
            public void run() {
                long cid = pBilibiliClient.getPAppAPI().view(Long.parseLong(getIntent().getExtras().getString("av"))).getData().getCid();
                videoPlayUrl = pBilibiliClient.getPPlayerAPI().videoPlayUrl(Long.parseLong(getIntent().getExtras().getString("av")), cid);
                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mTv.setText(videoPlayUrl.toString());
        }
    }
}
