package com.edusoho.playerdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.player.entity.PlayerParam;
import com.edusoho.cloud.player.listener.PlayerEventListener;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.ui.base.BaseMediaActivity;
import com.edusoho.playerdemo.util.Utils;

import java.util.Map;

public class PPTActivity extends BaseMediaActivity {

    public static void launchPlayer(Context context, ResourceBean resource) {
        Intent intent = new Intent(context, PPTActivity.class);
        intent.putExtra(MEDIA_NAME, resource.name);
        intent.putExtra(MEDIA_TYPE, resource.mediaType);
        intent.putExtra(MEDIA_RES_NO, resource.resNo);
        intent.putExtra(MEDIA_TOKEN, resource.token);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("PPT");
        if (getIntentResourceBen() != null) {
            setTitle(getIntentResourceBen().name);
        }
    }

    @Override
    public void initView() {
        super.initView();
        mControlView.setMediaType(ResourceType.PPT);
        mLlVideoControlLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLoadMedia() {
        Map<String, String> params = getMediaParams("ppt.json");

        PlayerParam.Builder builder = new PlayerParam.Builder();
        PlayerParam param = builder.setResNo(params.get("resNo"))
                .setToken(params.get("token"))
                .setInitPos(1)
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
