package com.edusoho.playerdemo.biz.download;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.playerdemo.bean.ResourceBean;

import java.util.List;

public interface DownloadService {

    List<ResourceBean> getDownloadResource();

    List<ResourceBean> getDownloadingResource();

    void deleteResourceByResourceBean(List<ResourceBean> selectedList);

    void cancelDownloadingResource(List<ResourceBean> data);

    /**
     * 数据初始化
     */
    List<ResourceBean> getPresentResourceData();

    List<ResourceBean> getUploadResourceData();

    List<ResourceBean> getManageResourceData();

    ResourceBean getPresentResourceDataByType(ResourceType resourceType);

    boolean isInitApp();

    void setAppStatus(boolean status);
}
