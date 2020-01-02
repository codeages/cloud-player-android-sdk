package com.edusoho.playerdemo.biz.download.dao;

import com.edusoho.cloud.core.utils.SharePrefUtils;

import static com.edusoho.playerdemo.DemoApplication.app;

public class DownloadDaoImpl implements DownloadDao {
    private static int    IS_INIT    = 1;
    private static String INIT_APP   = "init_app";
    private static String TABLE_NAME = "download_resource";

    private SharePrefUtils sharePrefUtils = SharePrefUtils.getInstance(app.getApplicationContext()).open(TABLE_NAME);

    @Override
    public void setResourceDownloadStatus(String resNo, int status) {
        sharePrefUtils.putInt(resNo, status);
    }

    @Override
    public int getResourceDownloadStatus(String resNo) {
        return sharePrefUtils.getInt(resNo);
    }

    @Override
    public boolean getAppStatus() {
        int result = sharePrefUtils.getInt(INIT_APP);
        return result == IS_INIT;
    }

    @Override
    public void setAppStatus(boolean b) {
        sharePrefUtils.putInt(INIT_APP, IS_INIT);
    }
}
