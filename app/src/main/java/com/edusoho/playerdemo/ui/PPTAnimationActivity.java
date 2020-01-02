package com.edusoho.playerdemo.ui;

import android.os.Bundle;
import android.view.View;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.player.entity.PlayerParam;
import com.edusoho.cloud.player.listener.PlayerEventListener;
import com.edusoho.playerdemo.ui.base.BaseMediaActivity;
import com.edusoho.playerdemo.util.Utils;

import java.util.Map;

public class PPTAnimationActivity extends BaseMediaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("PPT动画");
    }

    @Override
    public void initView() {
        super.initView();
        mControlView.setMediaType(ResourceType.PPT);
        mLlVideoControlLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLoadMedia() {
        Map<String, String> params = Utils.getParams(this, "ppt_animation.json");
        PlayerParam.Builder builder = new PlayerParam.Builder();
        PlayerParam param = builder.setResNo(params.get("resNo"))
                .setToken(params.get("token"))
                .setUserId("1")
                .setUserName("yourName")
                .addPlayerEventListener(mPlayerEventListener)
                .build();
        resourcePlayer.load(param);
    }

    protected final PlayerEventListener mPlayerEventListener = new PlayerEventListener() {
        @Override
        public void onPrepared(String metas) {

        }

        @Override
        public void onPageChanged(int page, int total) {
            mControlView.updateDocPageNumber(page, total);
        }
    };

}
