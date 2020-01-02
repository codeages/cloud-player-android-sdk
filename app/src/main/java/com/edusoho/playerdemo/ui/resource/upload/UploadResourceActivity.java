package com.edusoho.playerdemo.ui.resource.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.biz.download.DownloadService;
import com.edusoho.playerdemo.biz.download.DownloadServiceImpl;
import com.edusoho.playerdemo.ui.resource.base.BaseResourceActivity;
import com.edusoho.playerdemo.ui.resource.upload.dialog.UploadDialog;
import com.edusoho.playerdemo.ui.resource.upload.dialog.UploadProcessDialog;
import com.edusoho.playerdemo.util.FileUtils;
import com.edusoho.playerdemo.util.Utils;
import com.edusoho.playerdemo.view.SwipeItemLayout;

public class UploadResourceActivity extends BaseResourceActivity {
    public static String ACTION_MANAGE_TAG = "manage_activity";
    public static String ACTION_UPLOAD_TAG = "upload_activity";
    public static String ACTION_NAME       = "action_name";

    private RelativeLayout mRlTips;
    private TextView       mTvTipsContent;
    private String         mActionName = ACTION_UPLOAD_TAG;

    private int             process          = 0;
    private DownloadService mDownloadService = new DownloadServiceImpl();
    private Handler         mHandler         = new Handler();

    public static void launchManageActivity(Context context) {
        Intent intent = new Intent(context, UploadResourceActivity.class);
        intent.putExtra(ACTION_NAME, ACTION_MANAGE_TAG);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getIntent().getStringExtra(ACTION_NAME) != null) {
            mActionName = getIntent().getStringExtra(ACTION_NAME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_upload_resource;
    }

    @Override
    public void initView() {
        super.initView();

        initBackBtn();
        rvContent = findViewById(R.id.rv_content);
        mRlTips = findViewById(R.id.ll_tips);
        mTvTipsContent = findViewById(R.id.tv_tips_content);
        setRecyclerView();

        if (mActionName.equals(ACTION_UPLOAD_TAG)) {
            setTitle(R.string.upload_resource);
            setRightBtnEvent(mContext.getString(R.string.resource_upload), v -> {
                UploadDialog.newInstance(new Bundle()).setUploadDialogListener((dialog, resourceType) -> {
                    launchFileContent(resourceType);
                    dialog.dismiss();
                }).show(getSupportFragmentManager(), "UploadDialog");
            });
        }
        if (mActionName.equals(ACTION_MANAGE_TAG)) {
            setTitle(R.string.manage_recource);
            mTvTipsContent.setText(R.string.manage_recource_tips_content);
        }
    }

    @Override
    public void initData() {
        super.initData();
        resourceAdapter = new UploadResourceAdapter(this);
        rvContent.setAdapter(resourceAdapter);

        if (mActionName.equals(ACTION_UPLOAD_TAG)) {
            resourceAdapter.setData(mDownloadService.getUploadResourceData());
            setHeader(rvContent);
        }

        if (mActionName.equals(ACTION_MANAGE_TAG)) {
            resourceAdapter.setData(mDownloadService.getManageResourceData());
            resourceAdapter.hideResourceNo();
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        mRlTips.setOnClickListener(v -> mRlTips.setVisibility(View.GONE));
    }

    private void setHeader(RecyclerView rvContent) {
        View header = LayoutInflater.from(this).inflate(R.layout.upload_header, rvContent, false);
        resourceAdapter.setHeaderView(header);
    }

    private void setRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvContent.setLayoutManager(mLayoutManager);
        rvContent.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(rvContent.getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        rvContent.addItemDecoration(itemDecoration);
        if (mActionName.equals(ACTION_MANAGE_TAG)) {
            rvContent.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        }
    }

    private void launchFileContent(ResourceType type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (type == ResourceType.VIDEO) {
            intent.setType("video/*");
        }
        if (type == ResourceType.AUDIO) {
            intent.setType("audio/*");
        }
        if (type == ResourceType.DOCUMENT) {
            /*无类型限制*/
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, FileUtils.DOCUMENT_MINE_TYPES);
        }
        if (type == ResourceType.PPT) {
            /*无类型限制*/
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, FileUtils.PPT_MINE_TYPES);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, type.ordinal());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fileName;
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            /*使用第三方应用打开*/
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return;
            }
            /*演示上传逻辑*/
            fileName = FileUtils.getFileName(this, uri);
            /*演示上传逻辑*/
            setAdapterItemData(requestCode, fileName);
            /*真实上传资源逻辑*/
            //uploadResourceFile(FileUtils.getPathFromUri(this, uri));
        }
    }

    /**
     * 演示上传逻辑
     *
     * @param resourceType
     * @param fileName
     */
    private void setAdapterItemData(int resourceType, String fileName) {
        ResourceBean resourceBean = new ResourceBean();
        if (resourceType == ResourceType.VIDEO.ordinal()) {
            resourceBean = mDownloadService.getPresentResourceDataByType(ResourceType.VIDEO);
        }
        if (resourceType == ResourceType.AUDIO.ordinal()) {
            resourceBean = mDownloadService.getPresentResourceDataByType(ResourceType.AUDIO);
        }
        if (resourceType == ResourceType.DOCUMENT.ordinal()) {
            resourceBean = mDownloadService.getPresentResourceDataByType(ResourceType.DOCUMENT);
        }
        if (resourceType == ResourceType.PPT.ordinal()) {
            resourceBean = mDownloadService.getPresentResourceDataByType(ResourceType.PPT);
        }
        resourceBean.name = fileName;
        /*演示*/
        setItemDataProcess(resourceBean);
    }

    public void setItemDataProcess(ResourceBean resourceBean) {
        UploadProcessDialog uploadProcessDialog = UploadProcessDialog.newInstance(new Bundle());
        uploadProcessDialog.show(getSupportFragmentManager(), "uploading");
        uploadProcessDialog.setUploadDialogListener(dialog -> {
            dialog.dismiss();
            Utils.showToast(mContext, "上传成功，请下拉到最下面查看新创建的课时");
            resourceAdapter.addItemData(resourceBean);
            rvContent.scrollToPosition(resourceAdapter.getItemCount() - 1);
        });

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                process += 1;
                uploadProcessDialog.setProgress(process);
                if (process > 100) {
                    mHandler.removeCallbacks(this);
                    uploadProcessDialog.uploadFinish();
                    process = 0;
                } else {
                    mHandler.postDelayed(this, 10);
                }
            }
        };
        mHandler.postDelayed(runnable, 10);
    }

    /**
     * 真实上传资源逻辑
     *
     * @param pathName
     */
    private void uploadResourceFile(String pathName) {
        UploadProcessDialog uploadProcessDialog = UploadProcessDialog.newInstance(new Bundle())
                .setUploadDialogListener(new UploadProcessDialog.UploadDialogListener() {
                    @Override
                    public void uploadFinish(UploadProcessDialog dialog) {
                        Utils.showToast(mContext, mContext.getString(R.string.upload_succes_tips));
                        rvContent.scrollToPosition(resourceAdapter.getItemCount() - 1);
                        dialog.dismiss();
                    }

                    @Override
                    public void uploadCancel(UploadProcessDialog dialog) {
                        Utils.showToast(mContext, mContext.getString(R.string.upload_cancel_tips));
                        dialog.dismiss();
                    }

                    @Override
                    public void uploadFail(UploadProcessDialog dialog) {

                    }
                });
        uploadProcessDialog.show(getSupportFragmentManager(), "uploading");
        uploadProcessDialog.startUploadFile(pathName);
    }
}
