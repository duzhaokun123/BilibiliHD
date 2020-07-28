package com.duzhaokun123.bilibilihd.bases;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseSimpleAdapter<layout extends ViewDataBinding> extends RecyclerView.Adapter<BaseSimpleAdapter<layout>.ViewHolder> {
    public final String CLASS_NAME = this.getClass().getSimpleName();

    private Context mContext;

    public BaseSimpleAdapter(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

    @Nullable
    protected Activity getActivity() {
        return mContext instanceof Activity ? (Activity) mContext : null;
    }

    @Nullable
    protected BaseActivity<?> getBaseActivity() {
        return mContext instanceof BaseActivity ? (BaseActivity<?>) mContext : null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(mContext), initLayout(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        initData(holder.getBaseBind(), position);
        initView(holder.getBaseBind(), position);
    }

    protected abstract int initLayout();

    protected abstract void initView(@NonNull layout baseBind, int position);

    protected abstract void initData(@NonNull layout baseBind, int position);

    public class ViewHolder extends RecyclerView.ViewHolder {
        private layout baseBind;

        public ViewHolder(layout viewDataBinding) {
            super(viewDataBinding.getRoot());
            baseBind = viewDataBinding;
        }

        public layout getBaseBind() {
            return baseBind;
        }
    }
}
