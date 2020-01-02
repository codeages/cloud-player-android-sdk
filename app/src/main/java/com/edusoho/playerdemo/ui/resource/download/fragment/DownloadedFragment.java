package com.edusoho.playerdemo.ui.resource.download.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.biz.download.DownloadService;
import com.edusoho.playerdemo.biz.download.DownloadServiceImpl;
import com.edusoho.playerdemo.ui.resource.base.BaseFragment;
import com.edusoho.playerdemo.ui.resource.download.adapter.DownloadFragmentListener;
import com.edusoho.playerdemo.ui.resource.download.adapter.DownloadingAdapter;
import com.edusoho.playerdemo.ui.resource.upload.dialog.EditResourceDialog;

import java.util.List;

public class DownloadedFragment extends BaseFragment implements DownloadFragmentListener {
    public static String       CONTEXT_TYPE = "DownloadedFragment";
    private       RecyclerView rvDownloaded;
    private       View         mToolsLayout;
    private       TextView     mTvSelectAll;
    private       TextView     mTvDelete;

    private DownloadingAdapter mDownloadedAdapter;
    private DownloadService    mDownloadService    = new DownloadServiceImpl();
    private boolean            mIsSelectedFragment = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_downloaded);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        rvDownloaded = rootView.findViewById(R.id.rv_downloaded);
        mToolsLayout = rootView.findViewById(R.id.download_tools_layout);
        mTvSelectAll = rootView.findViewById(R.id.tv_select_all);
        mTvDelete = rootView.findViewById(R.id.tv_delete);

        setRecyclerView();
        setAdapter();

        mTvSelectAll.setOnClickListener(v -> {
            if (mTvSelectAll.getText().equals(getString(R.string.select_all))) {
                mTvSelectAll.setText(getString(R.string.cancel));
                mDownloadedAdapter.isSelectAll(true);
            } else {
                mTvSelectAll.setText(getString(R.string.select_all));
                mDownloadedAdapter.isSelectAll(false);
            }
        });

        mTvDelete.setOnClickListener(v -> {
            List<ResourceBean> selectedList = mDownloadedAdapter.getSelectResourceBean();
            if (selectedList == null || selectedList.isEmpty()) {
                return;
            }
            EditResourceDialog.newInstance(new Bundle())
                    .setTitle(mContext.getString(R.string.dialog_title_tips))
                    .setTips(mContext.getString(R.string.dialog_confirm_delete_tips))
                    .setUploadDialogListener(new EditResourceDialog.UploadDialogListener() {
                        @Override
                        public void onCancel(EditResourceDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onConfirm(EditResourceDialog dialog, String text) {
                            dialog.dismiss();
                            mDownloadService.deleteResourceByResourceBean(selectedList);
                            mDownloadedAdapter.updateData(mDownloadService.getDownloadResource());
                        }
                    }).show(getFragmentManager(), "alert");
        });

    }

    @Override
    public void onSelected(boolean isSelected) {
        this.mIsSelectedFragment = isSelected;
        new Handler().postDelayed(() -> {
            if (isSelected) {
                mTvSelectAll.setText(getString(R.string.select_all));
                mDownloadedAdapter.updateData(mDownloadService.getDownloadResource());
            }
        }, 500);
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
        mDownloadedAdapter = new DownloadingAdapter(mContext);
        mDownloadedAdapter.setContextType(CONTEXT_TYPE);
        rvDownloaded.setAdapter(mDownloadedAdapter);
        mDownloadedAdapter.setData(mDownloadService.getDownloadResource());

        mDownloadedAdapter.setOnItemClickListener((v, position) -> {
            ResourceBean resourceBean = mDownloadedAdapter.getData().get(position);
            resourceBean.launchPlayerActivity(mContext);
        });
    }

    private void setRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvDownloaded.setLayoutManager(mLayoutManager);
        rvDownloaded.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(rvDownloaded.getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvDownloaded.addItemDecoration(itemDecoration);
    }

    /**
     * 切换状态
     */
    @Override
    public void switchSelectStatus(boolean isEdit) {
        if (isEdit) {
            showBtnLayout();
            mDownloadedAdapter.setSelectShow(true);
        } else {
            hideBtnLayout();
            mDownloadedAdapter.setSelectShow(false);
        }
    }

    private void showBtnLayout() {
        mToolsLayout.setVisibility(View.VISIBLE);
    }

    private void hideBtnLayout() {
        mToolsLayout.setVisibility(View.GONE);
    }
}
