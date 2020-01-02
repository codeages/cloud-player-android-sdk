package com.edusoho.playerdemo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.edusoho.cloud.core.entity.ResourceDefinition;
import com.edusoho.cloud.player.entity.PlayerParam;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.adapter.SampleAdapter;
import com.edusoho.playerdemo.ui.VideoActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class TestDemoActivity extends VideoActivity implements ExpandableListView.OnChildClickListener {
    private SampleAdapter        sampleAdapter;
    private SampleAdapter.Sample mSelectedItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("测试示例");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_test_demo);
    }

    @Override
    public void initView() {
        super.initView();
        sampleAdapter = new SampleAdapter(this);
        ExpandableListView sampleListView = findViewById(R.id.sample_list);
        sampleListView.setAdapter(sampleAdapter);
        sampleListView.setOnChildClickListener(this);
    }

    private void initData() {
        List<SampleAdapter.Sample> params = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("test_demo_list.json");
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String str = sb.toString();
            params = new Gson().fromJson(str, new TypeToken<List<SampleAdapter.Sample>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        sampleAdapter.setSampleGroups(new SampleAdapter.SampleGroup(getString(R.string.sample_list_group_title),
                params));
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        SampleAdapter.Sample sample = (SampleAdapter.Sample) view.getTag();
        mSelectedItem = sample;
        onLoadMedia();
        return true;
    }

    @Override
    public void onLoadMedia() {
        if (mSelectedItem == null) {
            return;
        }
        mControlView.setDefaultCurrentDefinitionName(ResourceDefinition.SD.name());
        PlayerParam.Builder builder = new PlayerParam.Builder();
        builder.setResNo(mSelectedItem.resNo)
                .setToken(mSelectedItem.token)
                .setUserId("1")
                .setUserName("jesse")
                .setDefinition(ResourceDefinition.HD)
                .setRememberLastPos(true)
                .setInitPos(38)
                .addPlayerEventListener(mPlayerEventListener);

        if (mSelectedItem.getWatermarkLocation() != null) {
            builder.setWatermarkDrawable(getResources().getDrawable(R.drawable.watermark))
                    .setWatermarkHeight(80)
                    .setWatermarkWidth(200)
                    .setLocation(mSelectedItem.getWatermarkLocation());
        }
        if (!mSelectedItem.getFingerprint().isEmpty()) {
            builder.setFingerprint(mSelectedItem.getFingerprint(), 10 * 1000);
        }
        resourcePlayer.release();
        resourcePlayer.load(builder.build());
    }
}
