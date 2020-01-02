package com.edusoho.playerdemo.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.player.entity.PlayerParam;
import com.edusoho.cloud.player.entity.PlayerState;
import com.edusoho.cloud.player.listener.PlayerEventListener;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.ui.base.BaseMediaActivity;

import java.util.Map;

import androidx.annotation.RequiresApi;

import static android.view.View.VISIBLE;

public class AudioActivity extends BaseMediaActivity {
    public static final int STATE_PLAY   = 1;//播放
    public static final int STATE_PAUSE  = 2;//暂停
    public static final int STATE_STOP   = 3;//结束
    public static final int STATE_RESUME = 4;

    private int            mState;
    private boolean        mStateReady = false;
    private ObjectAnimator mAnimator;
    private View           mRotationAnim;

    public static void launchPlayer(Context context, ResourceBean resource) {
        Intent intent = new Intent(context, AudioActivity.class);
        intent.putExtra(MEDIA_NAME, resource.name);
        intent.putExtra(MEDIA_TYPE, resource.mediaType);
        intent.putExtra(MEDIA_RES_NO, resource.resNo);
        intent.putExtra(MEDIA_TOKEN, resource.token);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("音频");
        if (getIntentResourceBen() != null) {
            setTitle(getIntentResourceBen().name);
            mControlView.setToolbarTitle(getIntentResourceBen().name);
        }
    }

    @Override
    public void initView() {
        super.initView();
        mRotationAnim = findViewById(R.id.audio_rotation_anim);
        mRotationAnim.setVisibility(VISIBLE);
        mControlView.setMediaType(ResourceType.AUDIO);
        mControlView.showControllerBar(true);
        mLlVideoControlLayout.setVisibility(View.GONE);
        initAnimator();
    }

    @Override
    public void onLoadMedia() {
        Map<String, String> params = getMediaParams("audio.json");

        PlayerParam.Builder builder = new PlayerParam.Builder();
        PlayerParam param = builder.setResNo(params.get("resNo"))
                .setToken(params.get("token"))
                .addPlayerEventListener(mPlayerEventListener)
                .build();
        resourcePlayer.release();
        resourcePlayer.load(param);
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
                    mState = STATE_PAUSE;
                    mControlView.updateMediaBufferState();
                    mControlView.pauseMediaProgress();
                    mPdLoading.setVisibility(VISIBLE);
                    break;
                case PlayerState.STATE_READY:
                    mState = STATE_RESUME;
                    if (!mStateReady) {
                        mState = STATE_PLAY;
                        mStateReady = true;
                    }
                    if (!playWhenReady) {
                        mState = STATE_PAUSE;
                    }
                    mPdLoading.setVisibility(View.GONE);
                    mControlView.updateMediaProgress();
                    break;
                case PlayerState.STATE_ENDED:
                    mState = STATE_STOP;
                    mControlView.setVisibility(VISIBLE);
                    mPdLoading.setVisibility(View.GONE);
                    mControlView.resetCurrentPositionTime();
                    break;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                playMusic();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void playMusic() {
        if (mState == STATE_PLAY) {
            mAnimator.start();//动画开始
        }
        if (mState == STATE_RESUME) {
            mAnimator.resume();//动画重新开始
        }
        if (mState == STATE_PAUSE) {
            mAnimator.pause();//动画暂停
        }
        if (mState == STATE_STOP) {
            mAnimator.end();
            mStateReady = false;
        }
    }

    private void initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(mRotationAnim, "rotation", 0.0f, 360.0f);
        mAnimator.setDuration(5000);//设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        mAnimator.setInterpolator(new LinearInterpolator());// 匀速
    }
}
