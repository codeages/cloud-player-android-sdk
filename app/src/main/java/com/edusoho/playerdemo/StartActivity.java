package com.edusoho.playerdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.edusoho.playerdemo.biz.download.DownloadService;
import com.edusoho.playerdemo.biz.download.DownloadServiceImpl;
import com.edusoho.playerdemo.ui.NewMainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    DownloadService downloadService = new DownloadServiceImpl();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            startActivity();
            downloadService.setAppStatus(true);
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (downloadService.isInitApp()) {
            startActivity();
            mHandler.removeMessages(0);
            return;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }  //这里将自带的标题栏隐藏掉

        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    public void startActivity(){
        Intent intent = new Intent(this, NewMainActivity.class);
        startActivity(intent);
        finish();
    }
}
