package com.edusoho.playerdemo.ui.resource.upload;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.ui.resource.base.BaseResourceAdapter;
import com.edusoho.playerdemo.ui.resource.upload.dialog.EditResourceDialog;
import com.edusoho.playerdemo.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class UploadResourceAdapter extends BaseResourceAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;

    private Context                mContext;
    private List<ResourceBean>     resourceBeanList;
    private View                   mHeaderView;
    private boolean                isHideResourceNo = false;
    private UploadResourceActivity mActivity;

    public UploadResourceAdapter(UploadResourceActivity context) {
        mContext = context;
        mActivity = context;
        resourceBeanList = new ArrayList<>();
    }

    @Override
    public void setData(List<ResourceBean> resourceData) {
        resourceBeanList = resourceData;
    }

    @Override
    public void addItemData(ResourceBean item) {
        resourceBeanList.add(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new Holder(mHeaderView);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_upload_resource, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        ResourceBean resourceBean = resourceBeanList.get(getRealPosition(holder));
        Holder       mHolder      = (Holder) holder;
        if (isHideResourceNo) {
            mHolder.resourceNo.setVisibility(View.GONE);
        } else {
            mHolder.resourceNo.setText(String.format(mContext.getString(R.string.course_no), String.valueOf(getRealPosition(holder) + 1)));
        }
        mHolder.resourceTitle.setText(resourceBean.name);
        mHolder.playResourceBtn.setOnClickListener(v -> resourceBean.launchPlayerActivity(mContext));
        mHolder.mMainLayout.setOnClickListener(v -> {
            mHolder.notConfirmLayout.setVisibility(View.VISIBLE);
            mHolder.itemConfirmDeleteLayout.setVisibility(View.GONE);
            resourceBean.launchPlayerActivity(mContext);
        });
        mHolder.itemDeleteBtn.setOnClickListener(v -> {
            mHolder.notConfirmLayout.setVisibility(View.GONE);
            mHolder.itemConfirmDeleteLayout.setVisibility(View.VISIBLE);
        });

        mHolder.itemConfirmDeleteBtn.setOnClickListener(v -> {
            resourceBeanList.remove(getRealPosition(mHolder));
            notifyItemRemoved(getRealPosition(holder));
            Utils.showToast(mContext, mContext.getString(R.string.delete_succes_tips));
        });

        mHolder.itemEditBtn.setOnClickListener(v -> showEditDialog(getRealPosition(holder)));
    }

    @Override
    public void hideResourceNo() {
        isHideResourceNo = true;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? resourceBeanList.size() : resourceBeanList.size() + 1;
    }

    @Override
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView  resourceNo;
        TextView  resourceTitle;
        ImageView playResourceBtn;

        Button         itemEditBtn;
        Button         itemDeleteBtn;
        Button         itemConfirmDeleteBtn;
        LinearLayout   itemConfirmDeleteLayout;
        LinearLayout   notConfirmLayout;
        RelativeLayout mMainLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            resourceNo = itemView.findViewById(R.id.resource_no);
            resourceTitle = itemView.findViewById(R.id.resource_title);
            playResourceBtn = itemView.findViewById(R.id.btn_play_resource);

            itemConfirmDeleteLayout = itemView.findViewById(R.id.ll_delete_confirm);
            itemConfirmDeleteBtn = itemView.findViewById(R.id.btn_delete_confirm);
            itemDeleteBtn = itemView.findViewById(R.id.btn_item_delete);
            itemEditBtn = itemView.findViewById(R.id.btn_item_edit);
            notConfirmLayout = itemView.findViewById(R.id.ll_not_confirm);
            mMainLayout = itemView.findViewById(R.id.main);

        }
    }

    private void showEditDialog(int itemPosition) {
        EditResourceDialog.newInstance(new Bundle())
                .setTitle(mContext.getString(R.string.dialog_edit_title))
                .setUploadDialogListener(new EditResourceDialog.UploadDialogListener() {
                    @Override
                    public void onCancel(EditResourceDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onConfirm(EditResourceDialog dialog, String text) {
                        dialog.dismiss();
                        resourceBeanList.get(itemPosition).name = text;
                        notifyItemChanged(itemPosition);
                        Utils.showToast(mContext, mContext.getString(R.string.edit_sucess_tips));
                    }
                }).show(mActivity.getSupportFragmentManager(), "dialog");
    }
}
