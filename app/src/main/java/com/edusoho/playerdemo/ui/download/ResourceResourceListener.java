package com.edusoho.playerdemo.ui.download;

import android.os.Build;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.cloud.manager.ResourceTask;
import com.edusoho.cloud.manager.core.listener.ResourceListener;

public class ResourceResourceListener implements ResourceListener {

    ProgressBar progressBar;
    TextView    tvSpeed1;

    public ResourceResourceListener(ProgressBar progressBar, TextView tvSpeed1) {
        this.progressBar = progressBar;
        this.tvSpeed1 = tvSpeed1;
    }

    @Override
    public void taskStart(ResourceTask task) {
        Log.d("flag--", "taskStart: ");
    }

    @Override
    public void infoReady(ResourceTask task, long totalLength) {
        progressBar.setMax((int) totalLength);
        Log.d("flag--", "infoReady: ");
    }

    @Override
    public void progress(ResourceTask task, long currentOffset, String speed) {
        Log.d("flag--", "progress: ");
        tvSpeed1.setText(speed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress((int) currentOffset, true);
        } else {
            progressBar.setProgress((int) currentOffset);
        }
    }

    @Override
    public void taskEnd(ResourceTask task) {
        Log.d("flag--", "demo ResourceResourceListener taskEnd: " + task.getUrl());
    }
}
