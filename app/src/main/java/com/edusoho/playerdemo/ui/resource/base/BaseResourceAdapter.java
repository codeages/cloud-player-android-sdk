package com.edusoho.playerdemo.ui.resource.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.playerdemo.bean.ResourceBean;

import java.util.List;

public class BaseResourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public OnItemClickListener mOnItemClickListener;

    public void setData(List<ResourceBean> resourceData) {
    }

    public void updateData(List<ResourceBean> resourceData) {
    }

    public void setHeaderView(View headerView) {
    }

    public void hideResourceNo() {
    }

    public void addItemData(ResourceBean item) {
    }

    public void setContextType(String contextType) {
    }

    public List<ResourceBean> getData() {
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }

    public interface OnItemClickListener {
        /**
         * 列表item点击事件
         * @param view
         * @param position
         */
        void onItemClicked(View view, int position);
    }
}
