package com.duzhaokun123.bilibilihd.ui.play;

import android.os.Bundle;
import android.os.Message;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.FragmentDanmakuSendBinding;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;

import java.util.Objects;

// TODO: 20-4-13 添加更多选项
public class DanmakuSendFragment extends BaseFragment<FragmentDanmakuSendBinding> {
    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_danmaku_send;
    }

    @Override
    protected void initView() {
        baseBind.ll.setOnClickListener(v ->
                Objects.requireNonNull(Objects.requireNonNull(getBaseActivity()).getHandler())
                .sendEmptyMessage(3));
        baseBind.btnSend.setOnClickListener(v -> {
            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("message", baseBind.etDanmaku.getText().toString());
            message.setData(bundle);
            Objects.requireNonNull(Objects.requireNonNull(getBaseActivity()).getHandler()).sendMessage(message);
        });
    }

    @Override
    protected void initData() {

    }
}
