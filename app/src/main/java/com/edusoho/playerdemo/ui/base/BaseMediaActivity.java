package com.edusoho.playerdemo.ui.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.edusoho.cloud.core.entity.ResourceDefinition;
import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.player.view.ResourcePlayer;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.bean.ResourceBean;
import com.edusoho.playerdemo.listener.PlayerControlListener;
import com.edusoho.playerdemo.util.ControllerOptions;
import com.edusoho.playerdemo.util.Utils;
import com.edusoho.playerdemo.view.PlayerControlView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;

abstract public class BaseMediaActivity extends AppCompatActivity {
    public static String MEDIA_NAME   = "media_name";
    public static String MEDIA_TYPE   = "media_type";
    public static String MEDIA_RES_NO = "media_res_no";
    public static String MEDIA_TOKEN  = "media_token";

    public ProgressBar       mPdLoading;
    public LinearLayout      mLlVideoControlLayout;
    public LinearLayout      mLlControlLayout;
    public ResourcePlayer    resourcePlayer;
    public PlayerControlView mControlView;

    Context   mContext;
    ActionBar actionBar;
    public boolean mIsFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        actionBar = getSupportActionBar();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initEvent();
        onLoadMedia();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    protected void onDestroy() {
        if (resourcePlayer != null) {
            resourcePlayer.release();
        }
        super.onDestroy();
    }

    public ResourceBean getIntentResourceBen() {
        ResourceBean resourceBean = new ResourceBean();
        if (getIntent() != null) {
            resourceBean.name = getIntent().getStringExtra(MEDIA_NAME);
            resourceBean.mediaType = (ResourceType) getIntent().getSerializableExtra(MEDIA_TYPE);
            resourceBean.resNo = getIntent().getStringExtra(MEDIA_RES_NO);
            resourceBean.token = getIntent().getStringExtra(MEDIA_TOKEN);
        }
        return resourceBean;
    }

    public Map<String, String> getMediaParams(String jsonFileName) {
        Map<String, String> params = new HashMap<>();
        if (getIntentResourceBen() != null) {
            params.put("resNo", getIntentResourceBen().resNo);
            params.put("token", getIntentResourceBen().token);

        } else {
            params  = Utils.getParams(this, jsonFileName);
        }
        return params;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void initView() {
        mLlControlLayout = findViewById(R.id.control_view);
        mLlVideoControlLayout = findViewById(R.id.video_control);

        resourcePlayer = findViewById(R.id.resource_player);
        mPdLoading = findViewById(R.id.pd_loading);
        mControlView = findViewById(R.id.es_player_control_view);

        mControlView.setPlayerView(resourcePlayer);
        mControlView.setVisibility(VISIBLE);

        setControlViewOptions();
    }

    public void initEvent() {
    }

    abstract public void onLoadMedia();

    public void newSetRequestedOrientation(int orientation) {
        int requestedOrientation = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeScreenLayout(newConfig.orientation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (getWindowIsFullScreen()) {
                changeScreenLayout(getRequestedOrientation());
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 改变屏幕操作
     *
     * @param orientation
     */
    protected void changeScreenLayout(int orientation) {
        View viewParent = resourcePlayer.getRootView();
        if (viewParent == null) {
            return;
        }
        ViewGroup              parent = (ViewGroup) viewParent;
        ViewGroup.LayoutParams lp     = parent.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        parent.setLayoutParams(lp);

        visibleControlView(orientation == Configuration.ORIENTATION_LANDSCAPE ? View.GONE : VISIBLE);
        visibleActionBar(orientation != Configuration.ORIENTATION_LANDSCAPE);
        mControlView.updateControllerConfiguration(orientation);
    }

    private void visibleControlView(int visible) {
        mLlControlLayout.setVisibility(visible);
    }

    private void visibleActionBar(boolean visible) {
        if (visible) {
            actionBar.show();
            showStatusBar();
        } else {
            actionBar.hide();
            hideStatusBar();
        }
    }

    //全屏并且隐藏状态栏
    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        mIsFullScreen = true;
    }

    //全屏并且状态栏透明显示
    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        mIsFullScreen = false;
    }

    private boolean getWindowIsFullScreen() {
        return mIsFullScreen;
    }

    private void setControlViewOptions() {
        ControllerOptions options = new ControllerOptions.Builder()
                .addOption(ControllerOptions.RATE, true)
                .addOption(ControllerOptions.SCREEN, true)
                .build();
        mControlView.setControllerOptions(options);
        mControlView.setControllerListener(mPlayerControllerListener);
    }

    PlayerControlListener mPlayerControllerListener = new PlayerControlListener() {
        @Override
        public void onSeek(int position) {
            resourcePlayer.seekTo(position);
        }

        @Override
        public void onChangeScreen(int orientation) {
            newSetRequestedOrientation(orientation);
        }

        @Override
        public void onPlayStatusChange(boolean isPlay) {
            if (isPlay) {
                resourcePlayer.play();
            } else {
                resourcePlayer.pause();
            }
        }

        @Override
        public void onChangeRate(float rate) {
            resourcePlayer.setSpeed(rate);
        }

        @Override
        public void onChangePlaySource(ResourceDefinition definition) {
            resourcePlayer.switchDefinition(definition);
        }

        @Override
        public void onChangeOverlay(boolean isShow) {

        }

        @Override
        public void onChangePage(boolean isPrev) {
            if (isPrev) {
                resourcePlayer.prevPage();
            } else {
                resourcePlayer.nextPage();
            }
        }
    };
}
