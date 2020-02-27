package com.edusoho.playerdemo.biz.download;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.cloud.manager.Downloader;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.biz.download.dao.DownloadDao;
import com.edusoho.playerdemo.biz.download.dao.DownloadDaoImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.edusoho.playerdemo.DemoApplication.app;

public class DownloadServiceImpl implements DownloadService {
    DownloadDao mDao = new DownloadDaoImpl();

    @Override
    public List<ResourceBean> getDownloadResource() {
        List<ResourceBean> resources = new ArrayList<>();
        for (ResourceBean resourceBean : getPresentResourceData()) {
            if (resourceBean.getDownloadStatus() == DownloadInfo.Status.COMPLETED) {
                resources.add(resourceBean);
            }
        }
        return resources;
    }

    @Override
    public List<ResourceBean> getDownloadingResource() {
        List<ResourceBean> resources = new ArrayList<>();
        for (ResourceBean resourceBean : getPresentResourceData()) {
            if (resourceBean.getDownloadStatus() != DownloadInfo.Status.COMPLETED
                    && resourceBean.getDownloadStatus() != DownloadInfo.Status.NONE) {
                resources.add(resourceBean);
            }
        }
        return resources;
    }

    @Override
    public void deleteResourceByResourceBean(List<ResourceBean> selectedList) {
        for (ResourceBean resourceBean : selectedList) {
            resourceBean.setSelected(false);
            Downloader.delete(resourceBean.buildResourceTask());
        }
    }

    @Override
    public void cancelDownloadingResource(List<ResourceBean> data) {
        for (ResourceBean resourceBean : data) {
            resourceBean.setSelected(false);
            Downloader.cancel(resourceBean.buildResourceTask());
        }
    }

    @Override
    public List<ResourceBean> getPresentResourceData() {
        List<ResourceBean> params = getResourceByJsonFileName("present_resources.json");
        return params;
    }

    @Override
    public List<ResourceBean> getUploadResourceData() {
        List<ResourceBean> params = getResourceByJsonFileName("upload_resources.json");
        return params;
    }

    @Override
    public List<ResourceBean> getManageResourceData() {
        List<ResourceBean> params = getResourceByJsonFileName("present_resources.json");
        return params;
    }

    @Override
    public ResourceBean getPresentResourceDataByType(ResourceType resourceType) {
        List<ResourceBean> params           = getResourceByJsonFileName("upload_resources.json");
        List<ResourceBean> resourceBeanList = new ArrayList<>();
        for (ResourceBean resourceData : params) {
            if (resourceData.mediaType.equals(resourceType)) {
                resourceBeanList.add(resourceData);
            }
        }
        return resourceBeanList.get(0);
    }

    @Override
    public boolean isInitApp() {
        return mDao.getAppStatus();
    }

    @Override
    public void setAppStatus(boolean status) {
        mDao.setAppStatus(status);
    }

    private List<ResourceBean> getResourceByJsonFileName(String fileName) {
        List<ResourceBean> params = new ArrayList<>();
        try {
            InputStream    inputStream = app.getAssets().open(fileName);
            StringBuilder  sb          = new StringBuilder();
            String         line;
            BufferedReader br          = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String str = sb.toString();
            params = new Gson().fromJson(str, new TypeToken<List<ResourceBean>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }
}
