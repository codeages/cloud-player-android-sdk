package com.edusoho.playerdemo.ui.resource.download.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.ui.resource.base.BaseResourceAdapter;
import com.edusoho.playerdemo.widget.DownloadProgressButton;

import java.util.ArrayList;
import java.util.List;

import static com.edusoho.playerdemo.ui.resource.download.fragment.DownloadedFragment.CONTEXT_TYPE;

public class DownloadingAdapter extends BaseResourceAdapter implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;

    private Context            mContext;
    private List<ResourceBean> resourceBeanList;
    private View               mHeaderView;

    private String  mContextType;
    private boolean mSelectedShow       = false;
    private boolean isAllDownloadCancel = false;

    public DownloadingAdapter(Context context) {
        mContext = context;
        resourceBeanList = new ArrayList<>();
    }

    @Override
    public void setContextType(String contextType) {
        mContextType = contextType;
    }

    @Override
    public void setData(List<ResourceBean> resourceData) {
        resourceBeanList = resourceData;
    }

    @Override
    public List<ResourceBean> getData() {
        return resourceBeanList;
    }

    @Override
    public void updateData(List<ResourceBean> resourceData) {
        resourceBeanList.clear();
        resourceBeanList.addAll(resourceData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new DownloadingAdapter.Holder(mHeaderView, mOnItemClickListener);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_downloading_resource, parent, false);
        return new DownloadingAdapter.Holder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        ResourceBean              resourceBean = resourceBeanList.get(getRealPosition(holder));
        DownloadingAdapter.Holder mHolder      = (DownloadingAdapter.Holder) holder;

        if (mContextType.equals(CONTEXT_TYPE)) {
            mHolder.downloadButton.setVisibility(View.GONE);
        }
        mHolder.resourceTitle.setText(resourceBean.name);
        mHolder.resourceTitle.setTag(holder);
        mHolder.ivDownloadSelected.setChecked(resourceBean.isSelected());

        mHolder.downloadButton.setOnClickListener(this);
        mHolder.downloadButton.setTag(holder);

        DownloadInfo downloadInfo = resourceBean.getDownloadInfo();
        resourceBean.setDownloadStatus(downloadInfo.getStatus());
        mHolder.downloadButton.setMax((int) downloadInfo.getTotalLength());

        if (downloadInfo.getStatus() != DownloadInfo.Status.COMPLETED && downloadInfo.getStatus() != DownloadInfo.Status.NONE) {
            mHolder.resourceTitle.setTextColor(mContext.getResources().getColor(R.color.font_grey_color));
        }

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

        if (isAllDownloadCancel) {
            mHolder.downloadButton.setPause(false);
        }
        if (!mHolder.downloadButton.isButtonLock()) {
            isAllDownloadCancel = false;
        }
        /*选择框是否显示*/
        if (mSelectedShow) {
            mHolder.ivDownloadSelected.setVisibility(View.VISIBLE);
            mHolder.ivDownloadSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    resourceBean.setSelected(true);
                } else {
                    resourceBean.setSelected(false);
                }
            });
        } else {
            mHolder.ivDownloadSelected.setVisibility(View.GONE);
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

    public void isSelectAll(boolean b) {
        for (ResourceBean item : resourceBeanList) {
            item.setSelected(b);
        }
        notifyDataSetChanged();
    }

    public void setSelectShow(boolean b) {
        this.mSelectedShow = b;
        notifyDataSetChanged();
    }

    public List<ResourceBean> getSelectResourceBean() {
        List<ResourceBean> beans = new ArrayList<>();
        for (ResourceBean item : resourceBeanList) {
            if (item.isSelected()) {
                beans.add(item);
            }
        }
        return beans;
    }

    @Override
    public void onClick(View v) {
        if (mSelectedShow) {
            return;
        }
        mOnItemClickListener.onItemClicked(v, getRealPosition((RecyclerView.ViewHolder) v.getTag()));
    }

    public void cancelDownloadRecourse() {
        this.isAllDownloadCancel = true;
        notifyDataSetChanged();
    }

    public void removeItemData(int position) {
        resourceBeanList.remove(position);
        notifyItemRemoved(position);
    }

    public class Holder extends RecyclerView.ViewHolder {
        AppCompatCheckBox      ivDownloadSelected;
        TextView               resourceNo;
        TextView               resourceTitle;
        DownloadProgressButton downloadButton;

        public Holder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            ivDownloadSelected = itemView.findViewById(R.id.iv_download_selected);
            resourceNo = itemView.findViewById(R.id.resource_no);
            resourceTitle = itemView.findViewById(R.id.resource_title);
            downloadButton = itemView.findViewById(R.id.btn_download);

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
