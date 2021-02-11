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

public abstract class BaseSimpleAdapter<Layout extends ViewDataBinding> extends RecyclerView.Adapter<BaseSimpleAdapter<Layout>.ViewHolder> {
    public final String CLASS_NAME = this.getClass().getSimpleName();

    private final Context mContext;

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
        initView(holder.getBaseBind(), position);
        initData(holder.getBaseBind(), position);
    }

    protected abstract int initLayout();

    protected abstract void initView(@NonNull Layout baseBind, int position);

    protected abstract void initData(@NonNull Layout baseBind, int position);

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Layout baseBind;

        public ViewHolder(Layout viewDataBinding) {
            super(viewDataBinding.getRoot());
            baseBind = viewDataBinding;
        }

        public Layout getBaseBind() {
            return baseBind;
        }
    }
}
