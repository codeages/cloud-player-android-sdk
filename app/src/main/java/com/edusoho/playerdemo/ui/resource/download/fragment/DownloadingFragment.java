package com.edusoho.playerdemo.ui.resource.download.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.cloud.manager.ResourceTask;
import com.edusoho.cloud.manager.core.listener.ResourceListener;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.biz.download.DownloadService;
import com.edusoho.playerdemo.biz.download.DownloadServiceImpl;
import com.edusoho.playerdemo.ui.resource.base.BaseFragment;
import com.edusoho.playerdemo.ui.resource.download.adapter.DownloadFragmentListener;
import com.edusoho.playerdemo.ui.resource.download.adapter.DownloadingAdapter;
import com.edusoho.playerdemo.ui.resource.upload.dialog.EditResourceDialog;
import com.edusoho.playerdemo.widget.DownloadProgressButton;

import java.util.List;

public class DownloadingFragment extends BaseFragment implements DownloadFragmentListener {
    public static String CONTEXT_TYPE = "DownloadingFragment";

    private RecyclerView rvDownloading;
    private View         mToolsLayout;
    private TextView     mTvSelectAll;
    private TextView     mTvDelete;

    private DownloadingAdapter mDownloadingAdapter;
    private DownloadService    mDownloadService = new DownloadServiceImpl();
    private boolean            mIsSelectedFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_downloading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        rvDownloading = rootView.findViewById(R.id.rv_downloading);
        mToolsLayout = rootView.findViewById(R.id.download_tools_layout);
        mTvSelectAll = rootView.findViewById(R.id.tv_select_all);
        mTvDelete = rootView.findViewById(R.id.tv_delete);
        mTvSelectAll.setText(getString(R.string.pause_all));
        mTvDelete.setText(getString(R.string.clear_all));

        setRecyclerView();
        setAdapter();

        mTvSelectAll.setOnClickListener(v -> {
            mTvSelectAll.setText(getString(R.string.pause_all));
            mDownloadService.cancelDownloadingResource(mDownloadingAdapter.getData());
            mDownloadingAdapter.cancelDownloadRecourse();
        });

        mTvDelete.setOnClickListener(v -> showDeleteAllDialog());
    }

    private void showDeleteAllDialog() {
        List<ResourceBean> selectedList = mDownloadingAdapter.getData();
        if (selectedList == null || selectedList.isEmpty()) {
            return;
        }
        EditResourceDialog.newInstance(new Bundle())
                .setTitle(mContext.getString(R.string.dialog_title_tips)).setTips(mContext.getString(R.string.dialog_confirm_delete_tips))
                .setUploadDialogListener(new EditResourceDialog.UploadDialogListener() {
                    @Override
                    public void onCancel(EditResourceDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onConfirm(EditResourceDialog dialog, String text) {
                        dialog.dismiss();
                        mDownloadService.deleteResourceByResourceBean(selectedList);
                        mDownloadingAdapter.updateData(mDownloadService.getDownloadingResource());
                    }
                }).show(getFragmentManager(), "alert");
    }

    @Override
    public void onSelected(boolean isSelected) {
        this.mIsSelectedFragment = isSelected;
    }

    @Override
    public boolean isSelected() {
        return mIsSelectedFragment;
    }

    @Override
    public boolean isEditStatus() {
        return mToolsLayout.getVisibility() == View.GONE;
    }

    private void setAdapter() {
        mDownloadingAdapter = new DownloadingAdapter(mContext);
        mDownloadingAdapter.setContextType(CONTEXT_TYPE);
        rvDownloading.setAdapter(mDownloadingAdapter);
        mDownloadingAdapter.setData(mDownloadService.getDownloadingResource());

        mDownloadingAdapter.setOnItemClickListener((v, position) -> {
            if (v.getId() == R.id.btn_download) {
                DownloadProgressButton button       = (DownloadProgressButton) v;
                ResourceBean           resourceBean = mDownloadingAdapter.getData().get(position);
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

    private void setRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvDownloading.setLayoutManager(mLayoutManager);
        rvDownloading.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(rvDownloading.getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvDownloading.addItemDecoration(itemDecoration);
    }

    /**
     * 切换状态
     */
    @Override
    public void switchSelectStatus(boolean isEdit) {
        if (isEdit) {
            showBtnLayout();
        } else {
            hideBtnLayout();
        }
    }

    private void showBtnLayout() {
        mToolsLayout.setVisibility(View.VISIBLE);
    }

    private void hideBtnLayout() {
        mToolsLayout.setVisibility(View.GONE);
    }

    @Override
    protected ResourceListener getResourceListener(DownloadProgressButton button) {
        return new ResourceListener() {

            @Override
            public void taskStart(ResourceTask task) {
            }

            @Override
            public void infoReady(ResourceTask task, long totalLength) {
                int position = mDownloadingAdapter.getRealPosition((RecyclerView.ViewHolder) button.getTag());
                if (position < 0) {
                    return;
                }

                button.setMax((int) totalLength);
                button.setDownloadStatus(DownloadInfo.Status.PENDING);
                button.setProgress(1);
            }

            @Override
            public void progress(ResourceTask task, long currentOffset, String speed) {
                int position = mDownloadingAdapter.getRealPosition((RecyclerView.ViewHolder) button.getTag());
                if (position < 0) {
                    return;
                }

                button.setDownloadStatus(DownloadInfo.Status.RUNNING);
                button.setProgress((int) currentOffset);
            }

            @Override
            public void taskEnd(ResourceTask task) {
                int position = mDownloadingAdapter.getRealPosition((RecyclerView.ViewHolder) button.getTag());
                if (position < 0) {
                    return;
                }

                button.setDownloadStatus(DownloadInfo.Status.COMPLETED);

                setItemColorByTaskStatus(button, false);
                mDownloadingAdapter.removeItemData(position);
            }
        };
    }
}
