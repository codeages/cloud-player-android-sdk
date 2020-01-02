package com.edusoho.playerdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.edusoho.cloud.core.entity.ResourceDefinition;
import com.edusoho.cloud.core.entity.ResourceError;
import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.manager.Downloader;
import com.edusoho.cloud.player.entity.PlayerParam;
import com.edusoho.cloud.player.entity.PlayerState;
import com.edusoho.cloud.player.entity.WatermarkLocation;
import com.edusoho.cloud.player.listener.PlayerEventListener;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.ui.base.BaseMediaActivity;
import com.edusoho.playerdemo.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;

public class VideoActivity extends BaseMediaActivity {

    Button mBtnSwitchMedia;

    public static void launchPlayer(Context context, ResourceBean resource) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(MEDIA_NAME, resource.name);
        intent.putExtra(MEDIA_TYPE, resource.mediaType);
        intent.putExtra(MEDIA_RES_NO, resource.resNo);
        intent.putExtra(MEDIA_TOKEN, resource.token);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("视频");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        if (resourcePlayer != null) {
            resourcePlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public void initView() {
        super.initView();

        mControlView.setMediaType(ResourceType.VIDEO);
        mControlView.showControllerBar(true);
        mBtnSwitchMedia = findViewById(R.id.switch_audio_and_video);
        if (getIntentResourceBen() != null) {
            setTitle(getIntentResourceBen().name);
            mControlView.setToolbarTitle(getIntentResourceBen().name);
//            mControlView.setMediaType(getIntentResourceBen().mediaType);
            mBtnSwitchMedia.setVisibility(View.GONE);
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();

        mBtnSwitchMedia.setOnClickListener(view -> resourcePlayer.switchPlaylist());
    }

    @Override
    public void onLoadMedia() {
        Downloader.init(this);
        mControlView.setDefaultCurrentDefinitionName(ResourceDefinition.SD.name());
        Map<String, String> params = getMediaParams("download.json");
        PlayerParam.Builder builder = new PlayerParam.Builder();
        PlayerParam param = builder.setResNo(params.get("resNo"))
                .setToken(params.get("token"))
                .setUserId("1")
                .setUserName("yourName")
                .setDefinition(ResourceDefinition.SD)
//                .setRememberLastPos(true)
//                .setInitPos(59) //如setRememberLastPos和setInitPos同时设置，此时setRememberLastPos无效。
                .setWatermarkUrl("http://caitong.dev15.edusoho.cn/files/system/watermark_1575549746.jpg?version=4.0.9_")
                .setWatermarkWidth(200)
                .setWatermarkHeight(80)
                .setLocation(WatermarkLocation.TOP_RIGHT)
                .setFingerprint("your fingerprint", 10 * 1000)
                .addPlayerEventListener(mPlayerEventListener)
                .build();
        resourcePlayer.release();
        resourcePlayer.load(param);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (resourcePlayer != null) {
            if (mControlView.getPlaybackState() == PlayerState.STATE_READY) {
                resourcePlayer.play();
            } else if (mControlView.getPlaybackState() == PlayerState.STATE_ENDED || mControlView.getPlaybackState() == PlayerState.STATE_IDLE) {
                resourcePlayer.pause();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (resourcePlayer != null) {
            if (mControlView.getPlaybackState() == PlayerState.STATE_READY) {
                resourcePlayer.pause();
            }
        }
    }

    protected final PlayerEventListener mPlayerEventListener = new PlayerEventListener() {
        @Override
        public void onPrepared(String metas) {
            mControlView.resetCurrentPositionTime();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            mControlView.setPlayWhenReady(playWhenReady);
            mControlView.setPlaybackState(playbackState);
            switch (playbackState) {
                case PlayerState.STATE_IDLE:
                    break;
                case PlayerState.STATE_BUFFERING:
                    mControlView.updateMediaBufferState();
                    mControlView.pauseMediaProgress();
                    mPdLoading.setVisibility(VISIBLE);
                    break;
                case PlayerState.STATE_READY:
                    mPdLoading.setVisibility(View.GONE);
                    mControlView.updateMediaProgress();
                    break;
                case PlayerState.STATE_ENDED:
                    mControlView.setVisibility(VISIBLE);
                    mPdLoading.setVisibility(View.GONE);
                    mControlView.resetCurrentPositionTime();
                    break;
            }
        }

        @Override
        public void onVideoPrepared(List<ResourceDefinition> resources, Map<String, String> videoMetas) {
            mControlView.setResourceDefinition(resources);
        }

        @Override
        public void onSwitchPlaylistPrepared(String playlistType) {
            if (playlistType.toUpperCase().equals(ResourceType.AUDIO.name())) {
                mBtnSwitchMedia.setText("播放视频");
            } else {
                mBtnSwitchMedia.setText("播放音频");
            }
        }

        @Override
        public void onError(ResourceError error) {
            Log.e("qiqiuyun-player", "Error Code: " + error.getCode());
            Log.e("qiqiuyun-player", "Error Message: " + error.getMessage());
            Log.e("qiqiuyun-player", "Error TraceId: " + error.getTraceId());
        }
    };
}
