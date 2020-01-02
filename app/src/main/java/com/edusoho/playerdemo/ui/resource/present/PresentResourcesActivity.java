package com.edusoho.playerdemo.ui.resource.present;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.cloud.manager.Downloader;
import com.edusoho.cloud.manager.ResourceTask;
import com.edusoho.cloud.manager.core.listener.ResourceListener;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.biz.download.DownloadService;
import com.edusoho.playerdemo.biz.download.DownloadServiceImpl;
import com.edusoho.playerdemo.ui.resource.base.BaseResourceActivity;
import com.edusoho.playerdemo.widget.DownloadProgressButton;

public class PresentResourcesActivity extends BaseResourceActivity {
    DownloadService mDownloadService = new DownloadServiceImpl();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initBackBtn() {
        super.initBackBtn();
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_present_resource;
    }

    @Override
    public void initView() {
        super.initView();
        setTitle(R.string.present_resource);
        rvContent = findViewById(R.id.rv_content);
        initBackBtn();
        setRecyclerView();
    }

    @Override
    public void initData() {
        super.initData();
        resourceAdapter = new PresentResourceAdapter(this);
        rvContent.setAdapter(resourceAdapter);
        resourceAdapter.setData(mDownloadService.getPresentResourceData());
        setHeader(rvContent);

        resourceAdapter.setOnItemClickListener((view, position) -> {
            if (view.getId() == R.id.btn_download) {
                DownloadProgressButton button       = (DownloadProgressButton) view;
                ResourceBean           resourceBean = resourceAdapter.getData().get(position);
                if (!button.isPause()) {
                    setItemColorByTaskStatus(button, true);
                    setDownloadTask(button, resourceBean);
                    button.setPause(true);
                } else if (resourceBean.canCancelDownloadTask()) {
                    cancelDownloadTask(resourceBean);
                    button.setPause(false);
                }
            }
        });
    }

    private void setDownloadTask(DownloadProgressButton button, ResourceBean resourceBean) {
        Downloader.enqueue(resourceBean.buildResourceTask(), getResourceListener(button));
    }

    private void cancelDownloadTask(ResourceBean resourceBean) {
        Downloader.cancel(resourceBean.buildResourceTask());
    }

    private ResourceListener getResourceListener(DownloadProgressButton button) {
        return new ResourceListener() {

            @Override
            public void taskStart(ResourceTask task) {
            }

            @Override
            public void infoReady(ResourceTask task, long totalLength) {
                int position = resourceAdapter.getRealPosition((RecyclerView.ViewHolder) button.getTag());
                if (position < 0) {
                    return;
                }
                button.setMax((int) totalLength);
                button.setDownloadStatus(DownloadInfo.Status.PENDING);
                button.setProgress(1);

                ResourceBean resourceBean = resourceAdapter.getData().get(position);
                resourceBean.setDownloadStatus(DownloadInfo.Status.PENDING);
            }

            @Override
            public void progress(ResourceTask task, long currentOffset, String speed) {
                int position = resourceAdapter.getRealPosition((RecyclerView.ViewHolder) button.getTag());
                if (position < 0) return;
                button.setDownloadStatus(DownloadInfo.Status.RUNNING);
                button.setProgress((int) currentOffset);

                ResourceBean resourceBean = resourceAdapter.getData().get(position);
                resourceBean.setDownloadStatus(DownloadInfo.Status.RUNNING);
            }

            @Override
            public void taskEnd(ResourceTask task) {
                int position = resourceAdapter.getRealPosition((RecyclerView.ViewHolder) button.getTag());
                if (position < 0) {
                    return;
                }
                button.setDownloadStatus(DownloadInfo.Status.COMPLETED);

                ResourceBean resourceBean = resourceAdapter.getData().get(position);
                resourceBean.setDownloadStatus(DownloadInfo.Status.COMPLETED);

                setItemColorByTaskStatus(button, false);
                resourceAdapter.notifyItemChanged(position + 1);
            }
        };
    }

    private void setItemColorByTaskStatus(DownloadProgressButton button, boolean iReading) {
        RelativeLayout rootParent = (RelativeLayout) button.getParent();
        LinearLayout   parent     = (LinearLayout) rootParent.getChildAt(0);
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (iReading) {
                ((TextView) parent.getChildAt(i)).setTextColor(mContext.getResources().getColor(R.color.font_grey_color));
            } else {
                ((TextView) parent.getChildAt(i)).setTextColor(mContext.getResources().getColor(R.color.primary_font_color));
            }
        }
    }

    private void setHeader(RecyclerView rvContent) {
        View header = LayoutInflater.from(this).inflate(R.layout.present_header, rvContent, false);
        resourceAdapter.setHeaderView(header);
    }

    private void setRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvContent.setLayoutManager(mLayoutManager);
        rvContent.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(rvContent.getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        rvContent.addItemDecoration(itemDecoration);
    }
}
