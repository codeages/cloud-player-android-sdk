package com.edusoho.playerdemo.biz.download.dao;

public interface DownloadDao {
    void setResourceDownloadStatus(String resNo, int status);

    int getResourceDownloadStatus(String resNo);

    boolean getAppStatus();

    void setAppStatus(boolean status);
}
