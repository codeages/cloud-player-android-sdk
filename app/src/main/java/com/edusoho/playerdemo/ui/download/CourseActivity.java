package com.edusoho.playerdemo.ui.download;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.cloud.core.entity.ResourceError;
import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.cloud.manager.DownloadStatusUtils;
import com.edusoho.cloud.manager.Downloader;
import com.edusoho.cloud.manager.ResourceTask;
import com.edusoho.cloud.manager.Uploader;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CourseActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ProgressBar[] progressBars = new ProgressBar[3];
    ProgressBar   progressBar4;
    TextView[]    tvSpeeds     = new TextView[3];
    TextView      tvSpeed4;
    CheckBox      checkBox1;
    CheckBox      checkBox2;
    CheckBox      checkBox3;
    static final String DOWNLOAD_URL1 = "http://devtest.edusoho.cn/hls/14/clef/71861dfc110a11ea8227005056a76e64";
    static final String DOWNLOAD_URL2 = "http://d1.music.126.net/dmusic/CloudMusic_official_4.3.2.468990.apk";
    static final String DOWNLOAD_URL3 = "https://dldir1.qq.com/foxmail/work_weixin/wxwork_android_2.4.5" +
            ".5571_100001" +
            ".apk";

    ResourceTask mTask1;
    ResourceTask mTask2;
    ResourceTask mTask3;
    ResourceTask mTask4;

    List<ResourceTask> mCheckedTasks = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        progressBars[0] = findViewById(R.id.progressBar1);
        progressBars[1] = findViewById(R.id.progressBar2);
        progressBars[2] = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);
        tvSpeeds[0] = findViewById(R.id.tv_speed1);
        tvSpeeds[1] = findViewById(R.id.tv_speed2);
        tvSpeeds[2] = findViewById(R.id.tv_speed3);
        tvSpeed4 = findViewById(R.id.tv_speed4);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox1.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
        checkBox3.setOnCheckedChangeListener(this);
        initTasks();
    }

    private void initTasks() {
        mTask1 = createTask1();
        mTask2 = createTask2();
        mTask3 = createTask3();
        mTask4 = createTask4();
        progressBars[0].setTag(mTask1.getNo());
        progressBars[1].setTag(mTask2.getNo());
        progressBars[2].setTag(mTask3.getNo());
        tvSpeeds[0].setTag(mTask1.getNo());
        tvSpeeds[1].setTag(mTask2.getNo());
        tvSpeeds[2].setTag(mTask3.getNo());
    }

    private ResourceTask createTask1() {
        Map<String, String> params = Utils.getParams(this, "download.json");
        return new ResourceTask.Builder()
                .setNo(params.get("resNo"))
                .setToken(params.get("token"))
                .build();
    }

    private ResourceTask createTask2() {
        Map<String, String> params = Utils.getParams(this, "download1.json");
        return new ResourceTask.Builder()
                .setNo(params.get("resNo"))
                .setToken(params.get("token"))
                .build();
    }

    private ResourceTask createTask3() {
        Map<String, String> params = Utils.getParams(this, "subtitle.json");
        return new ResourceTask.Builder()
                .setNo(params.get("resNo"))
                .setToken(params.get("token"))
                .build();
    }

    private ResourceTask createTask4() {
        return new ResourceTask.Builder()
                .setUrl(DOWNLOAD_URL2)
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox1:
                if (isChecked) {
                    mCheckedTasks.add(mTask1);
                } else {
                    mCheckedTasks.remove(mTask1);
                }
                break;
            case R.id.checkBox2: {
                if (isChecked) {
                    mCheckedTasks.add(mTask2);
                } else {
                    mCheckedTasks.remove(mTask2);
                }
                break;
            }
            case R.id.checkBox3: {
                if (isChecked) {
                    mCheckedTasks.add(mTask3);
                } else {
                    mCheckedTasks.remove(mTask3);
                }
                break;
            }
        }
    }

    private static final String UPLOAD_START = "http://es3.cloud-test.edusoho.cn/api/upload/test/start";
    private static final String FINISH_START = "http://es3.cloud-test.edusoho.cn/api/upload/test/finish";

    private boolean isCancelled;

    public void onDownload1(View view) {
        File file = new File(Environment.getExternalStorageDirectory() + "/player-cache/vid1.mp4");
        progressBars[0].setMax(100);
        if (file.exists()) {
            Uploader.upload(UPLOAD_START, FINISH_START, "1232132asad13", file, new Uploader.OnUploadListener() {
                @Override
                public void onError(ResourceError error) {

                }

                @Override
                public void onSuccess(String result) {
                    Toast.makeText(CourseActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                }

                @Override
                public void progress(double percent) {
                    progressBars[0].setProgress((int) (percent * 100));
                }

                @Override
                public boolean isCancelled() {
                    return isCancelled;
                }
            });
        } else {
            throw new RuntimeException("FILE NOT EXISTS");
        }
//        Downloader.enqueue(mTask1, new ResourceResourceListener(progressBars[0], tvSpeeds[0]));
    }

    public void onDownload2(View view) {
        Downloader.enqueue(mTask2, new ResourceResourceListener(progressBars[1], tvSpeeds[1]));
    }

    public void onDownload3(View view) {
        Downloader.enqueue(mTask3, new ResourceResourceListener(progressBars[2], tvSpeeds[2]));
    }

    public void onDownload4(View view) {
        Downloader.enqueue(mTask4, new ResourceResourceListener(progressBar4, tvSpeed4));
    }

    public void onCancel1(View view) {
        isCancelled = true;
        if (mTask1 != null) {
            Downloader.cancel(mTask1);
        }
    }

    public void onCancel2(View view) {
        if (mTask2 != null) {
            Downloader.cancel(mTask2);
        }
    }

    public void onCancel3(View view) {
        if (mTask3 != null) {
            Downloader.cancel(mTask3);
        }
    }

    public void onCancel4(View view) {
        if (mTask4 != null) {
            Downloader.cancel(mTask4);
        }
    }

    public void onDelete1(View view) {
        Downloader.delete(mTask1);
    }

    public void onDelete2(View view) {
        Downloader.delete(mTask2);
    }

    public void onDelete3(View view) {
        Downloader.delete(mTask3);
    }

    public void onStatus3(View view) {
        DownloadInfo downloadInfo = DownloadStatusUtils.getStatus(mTask3);
        Log.d("flag--", "onStatus3 out result: " + downloadInfo.getStatus());
    }

    public void onDelete4(View view) {
        Downloader.delete(mTask4);
    }

    public void onStatus4(View view) {
        DownloadInfo downloadInfo = DownloadStatusUtils.getStatus(mTask4);
        Log.d("flag--", "onStatus3: " + downloadInfo.getStatus());
    }

    public void onSelectDownload(View view) {
        ResourceTask[] downloadTaskArray = new ResourceTask[mCheckedTasks.size()];
        List<ProgressBar> selectedProgressBars = new ArrayList<>();
        List<TextView> selectTextViews = new ArrayList<>();
        for (ResourceTask task : mCheckedTasks) {
            for (ProgressBar progressBar : progressBars) {
                if (task.getNo().equals(progressBar.getTag())) {
                    selectedProgressBars.add(progressBar);
                }
            }
            for (TextView tvSpeed : tvSpeeds) {
                if (task.getNo().equals(tvSpeed.getTag())) {
                    selectTextViews.add(tvSpeed);
                }
            }
        }
        Downloader.enqueue(mCheckedTasks.toArray(downloadTaskArray),
                new ResourceResourcesListener(selectedProgressBars, selectTextViews));
    }

    public void onSelectCancel(View view) {
        ResourceTask[] downloadTaskArray = new ResourceTask[mCheckedTasks.size()];
        Downloader.cancel(mCheckedTasks.toArray(downloadTaskArray));
    }

    public void onSelectDelete(View view) {
        ResourceTask[] downloadTaskArray = new ResourceTask[mCheckedTasks.size()];
        Downloader.cancel(mCheckedTasks.toArray(downloadTaskArray));
    }

    public void onClearAll(View view) {
        Downloader.deleteAll();
    }
}
