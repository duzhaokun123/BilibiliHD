package com.duzhaokun123.bilibilihd.utils;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class XRecyclerViewUtil {
    public static void notifyItemsChanged(XRecyclerView xRecyclerView, int to) {
        notifyItemsChanged(xRecyclerView, 0, to);
    }

    public static void notifyItemsChanged(XRecyclerView xRecyclerView, int from, int to) {
        if (from > to) {
            return;
        }

        for (int i = from; i <= to; i++) {
            xRecyclerView.notifyItemChanged(i);
        }
    }
}
