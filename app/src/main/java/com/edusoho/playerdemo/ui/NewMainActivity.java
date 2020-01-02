package com.edusoho.playerdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.ui.resource.present.PresentResourcesActivity;
import com.edusoho.playerdemo.ui.resource.upload.UploadResourceActivity;
import com.edusoho.playerdemo.ui.resource.download.DownloadedResourceActivity;
import com.edusoho.playerdemo.util.Utils;
import com.edusoho.playerdemo.widget.BannerViewPager;

import java.util.Arrays;


public class NewMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = findViewById(R.id.tv_title);
        mTitle.setText(R.string.app_demo_name);
        setBanner();
        findViewById(R.id.rl_show_courses).setOnClickListener(this::onClick4);
        findViewById(R.id.rl_upload_resource).setOnClickListener(this::onClick3);
        findViewById(R.id.rl_manage_resource).setOnClickListener(this::onClick2);
        findViewById(R.id.rl_downloaded_resource).setOnClickListener(this::onClick);
    }

    private void setBanner() {
        BannerViewPager bannerViewPager = findViewById(R.id.banner);
        bannerViewPager.setDelay(3000);
        bannerViewPager.setData(Arrays.asList(getResources().getDrawable(R.drawable.banner_1), getResources().getDrawable(R.drawable.banner_1), getResources().getDrawable(R.drawable.banner_1)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onClick(View v) {
        if (Utils.isFastClick()) {
            startActivity(new Intent(this, DownloadedResourceActivity.class));
        }
    }

    private void onClick2(View v) {
        if (Utils.isFastClick()) {
            UploadResourceActivity.launchManageActivity(this);
        }
    }

    private void onClick3(View v) {
        if (Utils.isFastClick()) {
            startActivity(new Intent(this, UploadResourceActivity.class));
        }
    }

    private void onClick4(View v) {
        if (Utils.isFastClick()) {
            startActivity(new Intent(this, PresentResourcesActivity.class));
        }
    }
}
