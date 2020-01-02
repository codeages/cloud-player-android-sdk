package com.edusoho.playerdemo.ui.resource.download.adapter;

public interface DownloadFragmentListener {

    void onSelected(boolean isSelected);

    boolean isSelected();

    boolean isEditStatus();

    void switchSelectStatus(boolean isEdit);
}
