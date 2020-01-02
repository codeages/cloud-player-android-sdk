package com.edusoho.playerdemo.ui.resource.present;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.ui.resource.base.BaseResourceAdapter;
import com.edusoho.playerdemo.widget.DownloadProgressButton;

import java.util.ArrayList;
import java.util.List;

public class PresentResourceAdapter extends BaseResourceAdapter implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;

    private Context            mContext;
    private List<ResourceBean> resourceBeanList;
    private View               mHeaderView;

    public PresentResourceAdapter(Context context) {
        mContext = context;
        resourceBeanList = new ArrayList<>();
    }

    @Override
    public void setData(List<ResourceBean> resourceData) {
        resourceBeanList = resourceData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new Holder(mHeaderView, mOnItemClickListener);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_present_resource, parent, false);
        return new Holder(view, mOnItemClickListener);
    }

    @Override
    public List<ResourceBean> getData() {
        return resourceBeanList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        ResourceBean resourceBean = resourceBeanList.get(getRealPosition(holder));
        Holder       mHolder      = (Holder) holder;
        mHolder.resourceNo.setText(String.format(mContext.getString(R.string.course_no), String.valueOf(getRealPosition(holder) + 1)));
        mHolder.resourceTitle.setText(resourceBean.name);
        mHolder.llResourceTitle.setOnClickListener(v -> resourceBean.launchPlayerActivity(mContext));
        mHolder.downloadButton.setOnClickListener(this);
        mHolder.downloadButton.setTag(holder);

        DownloadInfo downloadInfo = resourceBean.getDownloadInfo();
        resourceBean.setDownloadStatus(downloadInfo.getStatus());

        if (downloadInfo.getStatus() != DownloadInfo.Status.COMPLETED && downloadInfo.getStatus() != DownloadInfo.Status.NONE) {
            mHolder.resourceTitle.setTextColor(mContext.getResources().getColor(R.color.font_grey_color));
            mHolder.resourceNo.setTextColor(mContext.getResources().getColor(R.color.font_grey_color));
        }

        mHolder.downloadButton.setMax((int) downloadInfo.getTotalLength());
        if (downloadInfo.getStatus() == DownloadInfo.Status.PENDING || downloadInfo.getStatus() == DownloadInfo.Status.RUNNING) {
            mHolder.downloadButton.setProgress((int) downloadInfo.getCurrentOffset());
            mHolder.downloadButton.setDownloadStatus(downloadInfo.getStatus());
            mHolder.downloadButton.performClick();

        } else if (downloadInfo.getStatus() == DownloadInfo.Status.COMPLETED) {
            mHolder.downloadButton.setDownloadStatus(downloadInfo.getStatus());
        } else if (downloadInfo.getStatus() == DownloadInfo.Status.IDLE) {
            mHolder.downloadButton.setProgress((int) downloadInfo.getCurrentOffset() == 0 ? 1 : (int) downloadInfo.getCurrentOffset());
            mHolder.downloadButton.setDownloadStatus(downloadInfo.getStatus());
        } else {
            mHolder.downloadButton.setDownloadStatus(DownloadInfo.Status.NONE);
        }
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

    @Override
    public void onClick(View v) {
        mOnItemClickListener.onItemClicked(v, getRealPosition((RecyclerView.ViewHolder) v.getTag()));
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView               resourceNo;
        TextView               resourceTitle;
        LinearLayout           llResourceTitle;
        DownloadProgressButton downloadButton;

        public Holder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            if (itemView == mHeaderView) return;
            resourceNo = itemView.findViewById(R.id.resource_no);
            resourceTitle = itemView.findViewById(R.id.resource_title);
            downloadButton = itemView.findViewById(R.id.btn_download);
            llResourceTitle = itemView.findViewById(R.id.ll_resource_title);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClicked(v, position);
                    }
                }
            });

        }
    }
}
