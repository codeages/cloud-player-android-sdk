package com.edusoho.playerdemo.bean;

import android.content.Context;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.cloud.manager.DownloadStatusUtils;
import com.edusoho.cloud.manager.ResourceTask;
import com.edusoho.playerdemo.ui.AudioActivity;
import com.edusoho.playerdemo.ui.DocumentActivity;
import com.edusoho.playerdemo.ui.PPTActivity;
import com.edusoho.playerdemo.ui.VideoActivity;
import com.edusoho.playerdemo.util.Utils;

import java.io.Serializable;

public class ResourceBean implements Serializable {

    public  String              name;
    public  String              resNo;
    public  String              token;
    public  ResourceType        mediaType;
    private boolean             isSelected = false;


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public DownloadInfo.Status getDownloadStatus() {
        return getDownloadInfo().status;
    }

    public boolean canCancelDownloadTask() {
        return getDownloadStatus() == DownloadInfo.Status.RUNNING
                || getDownloadStatus() == DownloadInfo.Status.PENDING
                || getDownloadStatus() == DownloadInfo.Status.IDLE;
    }

    public void launchPlayerActivity(Context context) {
        if (!Utils.isFastClick()) {
            return;
        }
        switch (mediaType) {
            case VIDEO:
                VideoActivity.launchPlayer(context, this);
                break;
            case AUDIO:
                AudioActivity.launchPlayer(context, this);
                break;
            case DOCUMENT:
                DocumentActivity.launchPlayer(context, this);
                break;
            case PPT:
                PPTActivity.launchPlayer(context, this);
                break;
            default:
                break;
        }
    }

    public DownloadInfo getDownloadInfo() {
        return getResourceDownloadStatus(buildResourceTask());
    }

    public ResourceTask buildResourceTask() {
        return new ResourceTask.Builder().setNo(resNo)
                .setToken(token)
                .build();
    }

    private DownloadInfo getResourceDownloadStatus(ResourceTask task) {
        return DownloadStatusUtils.getStatus(task);
    }
}
