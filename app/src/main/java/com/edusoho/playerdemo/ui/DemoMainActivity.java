package com.edusoho.playerdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edusoho.cloud.manager.Downloader;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.ui.download.CourseActivity;
import com.edusoho.playerdemo.view.TestDemoActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DemoMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);
        Downloader.init(getApplicationContext());
    }

    public void onVideoClick(View v) {
        startActivity(new Intent(this, VideoActivity.class));
    }

    public void onAudioClick(View v) {
        startActivity(new Intent(this, AudioActivity.class));
    }

    public void onDocumentClick(View v) {
        startActivity(new Intent(this, DocumentActivity.class));
    }

    public void onPPTClick(View v) {
        startActivity(new Intent(this, PPTActivity.class));
    }

    public void onPPTAnimationClick(View v) {
        startActivity(new Intent(this, PPTAnimationActivity.class));
    }

    public void onTestDemoClick(View v) {
        startActivity(new Intent(this, TestDemoActivity.class));
    }

    public void onTestDownloadClick(View v) {
        startActivity(new Intent(this, CourseActivity.class));
    }
}
