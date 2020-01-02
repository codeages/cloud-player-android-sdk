package com.edusoho.playerdemo.ui.download;

import android.os.Build;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.cloud.manager.ResourceTask;
import com.edusoho.cloud.manager.core.listener.ResourceListener;

import java.util.List;

public class ResourceResourcesListener implements ResourceListener {

    List<ProgressBar> progressBars;
    List<TextView>    tvSpeeds;

    public ResourceResourcesListener(List<ProgressBar> progressBars, List<TextView> tvSpeeds) {
        this.progressBars = progressBars;
        this.tvSpeeds = tvSpeeds;
    }

    @Override
    public void taskStart(ResourceTask task) {
        Log.d("flag--", "taskStart: ");
    }

    @Override
    public void infoReady(ResourceTask task, long totalLength) {
        getProgressBarByResNo(task.getNo()).setMax((int) totalLength);
    }

    @Override
    public void progress(ResourceTask task, long currentOffset, String speed) {
        Log.d("flag--", "progress: ");
        getSpeedTextByResNo(task.getNo()).setText(speed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getProgressBarByResNo(task.getNo()).setProgress((int) currentOffset, true);
        } else {
            getProgressBarByResNo(task.getNo()).setProgress((int) currentOffset);
        }
    }

    @Override
    public void taskEnd(ResourceTask task) {
        Log.d("flag--", "demo ResourceResourceListener taskEnd: " + task.getUrl());
    }

    private ProgressBar getProgressBarByResNo(String resNo) {
        for (ProgressBar progressBar : progressBars) {
            if (resNo.equals(progressBar.getTag())) {
                return progressBar;
            }
        }
        throw new RuntimeException("can not find ProgressBar");
    }

    private TextView getSpeedTextByResNo(String resNo) {
        for (TextView textView : tvSpeeds) {
            if (resNo.equals(textView.getTag())) {
                return textView;
            }
        }
        throw new RuntimeException("can not find ProgressBar");
    }
}
