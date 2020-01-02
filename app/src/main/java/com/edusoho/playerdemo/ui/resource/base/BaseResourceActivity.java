package com.edusoho.playerdemo.ui.resource.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edusoho.playerdemo.R;

public abstract class BaseResourceActivity extends AppCompatActivity {


    public Toolbar      mToolbar;
    public LinearLayout mIvBack;
    public TextView     mTitle;
    public FrameLayout  mFlCustomView;
    public TextView     mBtnRight;

    public Context             mContext;
    public RecyclerView        rvContent;
    public BaseResourceAdapter resourceAdapter;

    private static String[] PERMISSIONS_STORAGE     = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int      REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_toolbar_layout);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        initView();
        initData();
        initEvent();
    }

    @Override
    public void setTitle(int resId) {
        mTitle.setText(getString(resId));
    }

    public void initBackBtn() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(v -> finish());
    }

    public void setRightBtnEvent(CharSequence sequence, View.OnClickListener listener) {
        mBtnRight.setText(sequence);
        mBtnRight.setVisibility(View.VISIBLE);
        mBtnRight.setOnClickListener(listener);
    }

    abstract public int getContentViewResId();

    public void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mIvBack = findViewById(R.id.iv_back);
        mTitle = findViewById(R.id.tv_title);
        mBtnRight = findViewById(R.id.btn_right);
        mFlCustomView = findViewById(R.id.fl_custom_layout);
        setContentView();
    }

    public void initData() {
    }

    public void initEvent() {
    }

    public void setContentView() {
        View view = LayoutInflater.from(mContext).inflate(getContentViewResId(), null, false);
        mFlCustomView.removeAllViews();
        mFlCustomView.addView(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != 0) {
                    Toast.makeText(mContext, mContext.getText(R.string.write_external_storage), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
}
