package com.edusoho.playerdemo.ui.resource.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edusoho.cloud.manager.Downloader;
import com.edusoho.cloud.manager.core.listener.ResourceListener;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.widget.DownloadProgressButton;

public class BaseFragment extends Fragment {
    private int mLayoutResId;

    protected View rootView;

    protected Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mLayoutResId != 0) {
            super.onCreateView(inflater, container, savedInstanceState);
            rootView = inflater.inflate(mLayoutResId, container, false);
            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void setContentView(@LayoutRes int layoutId) {
        mLayoutResId = layoutId;
    }

    protected void setDownloadTask(DownloadProgressButton button, ResourceBean resourceBean) {
        Downloader.enqueue(resourceBean.buildResourceTask(), getResourceListener(button));
    }

    protected ResourceListener getResourceListener(DownloadProgressButton button) {
        return null;
    }

    protected void cancelDownloadTask(ResourceBean resourceBean) {
        Downloader.cancel(resourceBean.buildResourceTask());
    }

    protected void setItemColorByTaskStatus(DownloadProgressButton button, boolean isReading) {
        RelativeLayout parent = (RelativeLayout) button.getParent();
        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            if (isReading) {
                ((TextView) parent.getChildAt(i)).setTextColor(mContext.getResources().getColor(R.color.font_grey_color));
            } else {
                ((TextView) parent.getChildAt(i)).setTextColor(mContext.getResources().getColor(R.color.primary_font_color));
            }
        }
    }
}
