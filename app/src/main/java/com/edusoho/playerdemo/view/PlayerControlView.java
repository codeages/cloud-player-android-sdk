package com.edusoho.playerdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.edusoho.cloud.core.entity.ResourceDefinition;
import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.cloud.player.entity.PlayerState;
import com.edusoho.cloud.player.view.ResourcePlayer;
import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.listener.DefinitionListAdapter;
import com.edusoho.playerdemo.listener.PlayerControlListener;
import com.edusoho.playerdemo.util.ControllerOptions;
import com.edusoho.playerdemo.util.Strings;
import com.edusoho.playerdemo.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerControlView extends FrameLayout {
    private static final String TAG                   = "PlayerControlView";
    private static final int    FADE_OUT              = 1;
    private static final int    DEFAULT_TIMEOUT_COUNT = 5000;

    private Context        mContext;
    private LinearLayout   mToolsView;
    private RelativeLayout mToolbarView;
    private LinearLayout   mToolbarBack;
    private TextView       mToolbarTitle;
    private ImageView      mPlayBtn;
    private TextView       mTimeView;
    private SeekBar        mProgressView;
    private TextView       mDefinitionListView;
    private TextView       mRateView;
    private CheckBox       mScreenChangeView;
    private View           mDocToolsView;
    private TextView       mDocNextPage;
    private TextView       mDocPrewPage;
    private CheckBox       mDocFullScreen;
    private TextView       mDocPageNumber;

    private       ResourcePlayer                  mPlayerView;
    private       PlayerControlListener           mPlayerControllerListener;
    private       Handler                         mHandler;
    private       float[]                         defaultRateArray;
    private       int                             mCurrentRateIndex = 1;
    private       boolean                         isSeekByUser;
    private       int                             mSecProcess;
    private final Runnable                        updateProgressAction;
    private       boolean                         isAttachedToWindow;
    private       GestureDetectorCompat           mGestureDetector;
    private       int                             mOrientation;
    private       int                             mCurrentPositionTime;
    private       ControllerOptions               mControllerOptions;
    private       PopupWindow                     definitionListPopupWindow;
    private       Map<String, ResourceDefinition> mResourceDefinitionList;
    private       String                          mCurrentDefinitionName;
    private       ResourceType                    mMediaType;
    private       boolean                         mPlayWhenReady;
    private       int                             mPlaybackState;

    public PlayerControlView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        updateProgressAction = this::updateMediaProgress;
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mOrientation = getContext().getResources().getConfiguration().orientation;
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);

        LayoutInflater.from(mContext).inflate(R.layout.player_control_view, this);
        mToolsView = findViewById(R.id.ll_controller_tools);
        mToolbarView = findViewById(R.id.tl_toolbar_layout);
        mToolbarBack = findViewById(R.id.iv_back);
        mToolbarTitle = findViewById(R.id.tv_title);
        if (mOrientation != Configuration.ORIENTATION_LANDSCAPE) {
            mToolbarView.setVisibility(GONE);
        }
        mPlayBtn = findViewById(R.id.tv_controller_play);
        mTimeView = findViewById(R.id.tv_controller_time);
        mProgressView = findViewById(R.id.sb_controller_progress);
        mDefinitionListView = findViewById(R.id.tv_controller_definition_list);
        mRateView = findViewById(R.id.tv_controller_rate);
        mScreenChangeView = findViewById(R.id.cb_controller_screen);

        mDocToolsView = findViewById(R.id.doc_control);
        mDocNextPage = findViewById(R.id.btn_next_page);
        mDocPrewPage = findViewById(R.id.btn_prev_page);
        mDocFullScreen = findViewById(R.id.cb_doc_screen);
        mDocPageNumber = findViewById(R.id.tv_page_number);

    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        //设置默认倍速
        setDefaultRateArray(new float[]{0.5f, 1.0f, 1.5f, 2.0f});
        updateRateView(defaultRateArray[mCurrentRateIndex]);
        setControllerOptions(ControllerOptions.getDefault());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case FADE_OUT:
                        hideOverlay();
                }
            }
        };
    }

    private void initEvent() {
        bindControllerListener();
        mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);
        mGestureDetector.setOnDoubleTapListener(getScreenDoubleTapListener());
    }

    private void bindControllerListener() {
        mPlayBtn.setOnClickListener(getPlayClickListener());
        mDocPrewPage.setOnClickListener(getDocPrevPageListener());
        mDocNextPage.setOnClickListener(getDocNextPageListener());
        mDocFullScreen.setOnCheckedChangeListener(getDocFullScreenPageListener());
        ((View) mRateView.getParent()).setOnClickListener(getRateClickListener());
        ((View) mDefinitionListView.getParent()).setOnClickListener(getDefinitionListClickListener());
        mScreenChangeView.setOnCheckedChangeListener(getOnScreenChangeListener());
        mProgressView.setOnSeekBarChangeListener(getOnProgressChangeListener());
        mToolsView.setOnTouchListener((v, event) -> {
            hideOverlayDelayed(DEFAULT_TIMEOUT_COUNT);
            return true;
        });
        mToolbarBack.setOnClickListener(v -> getControllerListener().onChangeScreen(Configuration.ORIENTATION_PORTRAIT));
    }

    protected OnClickListener getPlayClickListener() {
        return v -> {
            mPlayBtn.setSelected(!mPlayBtn.isSelected());
            setProgressByPlayStatus(mPlayBtn.isSelected());
            getControllerListener().onPlayStatusChange(getPlaybackState() == PlayerState.STATE_ENDED ? true :
                    mPlayBtn.isSelected());
        };
    }

    protected OnClickListener getDefinitionListClickListener() {
        return v -> showPopupWindows();
    }

    protected OnClickListener getRateClickListener() {
        return v -> {
            mCurrentRateIndex = mCurrentRateIndex + 1;
            if (mCurrentRateIndex >= defaultRateArray.length) {
                mCurrentRateIndex = 0;
            }
            updateRateView(defaultRateArray[mCurrentRateIndex]);
            getControllerListener().onChangeRate(defaultRateArray[mCurrentRateIndex]);
        };
    }

    protected CompoundButton.OnCheckedChangeListener getOnScreenChangeListener() {
        return (buttonView, isChecked) -> {
            getControllerListener().onChangeScreen(isChecked ?
                    Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT);
            mToolbarView.setVisibility(isChecked ? VISIBLE : GONE);
        };
    }

    protected SeekBar.OnSeekBarChangeListener getOnProgressChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                isSeekByUser = fromUser;
                mCurrentPositionTime = progress;
                updateTime(progress, seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekByUser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekByUser = false;
                getControllerListener().onSeek(seekBar.getProgress());
            }
        };
    }

    protected GestureDetector.OnDoubleTapListener getScreenDoubleTapListener() {
        return new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mPlayBtn.setSelected(!mPlayBtn.isSelected());
                setProgressByPlayStatus(mPlayBtn.isSelected());
                getControllerListener().onPlayStatusChange(mPlayBtn.isSelected());
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        };
    }

    protected OnClickListener getDocPrevPageListener() {
        return view -> getControllerListener().onChangePage(true);
    }

    protected OnClickListener getDocNextPageListener() {
        return view -> getControllerListener().onChangePage(false);
    }

    protected CompoundButton.OnCheckedChangeListener getDocFullScreenPageListener() {
        return (buttonView, isChecked) -> {
            getControllerListener().onChangeScreen(isChecked ?
                    Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT);
            mToolbarView.setVisibility(GONE);
//            mToolbarView.setVisibility(isChecked ? VISIBLE : GONE);
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        if (isDocControlView()) {
            return true;
        }
        onTouchEventBySelf(event);
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
    }

    public void showControllerBar(boolean show) {
        if (show) {
            showOverlay(DEFAULT_TIMEOUT_COUNT);
        } else {
            hideOverlayDelayed(DEFAULT_TIMEOUT_COUNT);
        }
    }

    public void updateControllerConfiguration(int orientation) {
        if (mMediaType == ResourceType.AUDIO) {
            return;
        }
        mOrientation = orientation;
        if (isDocControlView()) {
            mDocFullScreen.setChecked(orientation == Configuration.ORIENTATION_LANDSCAPE);
            return;
        }
        mScreenChangeView.setChecked(orientation == Configuration.ORIENTATION_LANDSCAPE);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ((View) mRateView.getParent()).setVisibility(GONE);
            ((View) mDefinitionListView.getParent()).setVisibility(GONE);
            mScreenChangeView.setVisibility(mControllerOptions.getOption(ControllerOptions.SCREEN) ? VISIBLE : GONE);
            return;
        }
        ((View) mRateView.getParent()).setVisibility(mControllerOptions.getOption(ControllerOptions.RATE) ? VISIBLE :
                GONE);
        ((View) mDefinitionListView.getParent()).setVisibility(mResourceDefinitionList != null && !mResourceDefinitionList.isEmpty() ? VISIBLE : GONE);
    }

    public void updatePlayStatus(boolean isPlay) {
        mPlayBtn.setSelected(isPlay);
    }

    public void setDefaultRateArray(float[] defaultRateArray) {
        this.defaultRateArray = defaultRateArray;
    }

    public void updateDocPageNumber(int currentPageNumber, int total) {
        mDocPageNumber.setText(String.format("%d / %d", currentPageNumber, total));
    }

    protected void updateRateView(float rate) {
        mRateView.setText(rate + "x");
    }

    protected void showPopupWindows() {
        if (definitionListPopupWindow == null) {
            List<ResourceDefinition> definitionList = new ArrayList();
            for (Map.Entry<String, ResourceDefinition> entry : mResourceDefinitionList.entrySet()) {
                if (mCurrentDefinitionName.equals(entry.getKey())) {
                    continue;
                }
                definitionList.add(entry.getValue());
            }
            definitionListPopupWindow = initDefinitionPopupWindows(definitionList, mDefinitionListView.getWidth(),
                    mDefinitionListView.getHeight());
        }

        definitionListPopupWindow.showAsDropDown(mDefinitionListView, 0, 0);
    }

    protected PopupWindow initDefinitionPopupWindows(final List<ResourceDefinition> definitionInfoLists, int width,
                                                     int height) {
        RecyclerView listView = new RecyclerView(getContext());
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.addItemDecoration(new DefinitionListDecoration(getContext(), DefinitionListDecoration.VERTICAL_LIST,
                R.drawable.definition_list_decoration));
        DefinitionListAdapter adapter = new DefinitionListAdapter(getContext(), definitionInfoLists);
        adapter.setOnItemClickListener(position -> {
            ResourceDefinition definition = definitionInfoLists.get(position);
            mCurrentDefinitionName = definition.name();
            updateDefinitionListView(mCurrentDefinitionName);
            getControllerListener().onChangePlaySource(definition);
            hidePopWindows();
        });
        listView.setAdapter(adapter);

        PopupWindow popupWindow = new PopupWindow(getContext());
        popupWindow.setWidth(width);
        int decoration = getContext().getResources().getDimensionPixelOffset(R.dimen.definition_list_decoration);
        popupWindow.setHeight((height + decoration) * definitionInfoLists.size());
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(listView);

        return popupWindow;
    }

    protected void hidePopWindows() {
        if (definitionListPopupWindow != null) {
            definitionListPopupWindow.dismiss();
            definitionListPopupWindow = null;
        }
    }

    protected void updateDefinitionListView(String definitionIndex) {
        if (TextUtils.isEmpty(definitionIndex) || !mResourceDefinitionList.containsKey(mCurrentDefinitionName)) {
            return;
        }
        mDefinitionListView.setText(Utils.getDefinitionStringName(
                mContext,
                mResourceDefinitionList.get(mCurrentDefinitionName).name()));
    }

    private void updateTime(int position, int duration) {
        mTimeView.setText(String.format("%s/%s", Strings.secondToString(position), Strings.secondToString(duration)));
    }

    public void updateMediaBufferState() {
        mSecProcess = (int) getPlayer().getBufferedPosition();
        mProgressView.setSecondaryProgress(mSecProcess);
    }

    public void pauseMediaProgress() {
        updatePlayStatus(false);
        removeCallbacks(updateProgressAction);
    }

    public void updateMediaProgress() {
        if (getVisibility() != VISIBLE || !isAttachedToWindow
                || getPlayer() == null
                || !getPlayWhenReady()) {
            return;
        }
        updateMediaBufferState();
        updateProgress(getCurrentPositionTime(), (int) getPlayerDuration());
        updatePlayStatus(false);
        removeCallbacks(updateProgressAction);
        int playbackState = getPlayer() == null ? PlayerState.STATE_IDLE : getPlaybackState();
        if (playbackState != PlayerState.STATE_IDLE && playbackState != PlayerState.STATE_ENDED) {
            postDelayedProgressByCurrentRateIndex();
        }
    }

    public void resetCurrentPositionTime() {
        mCurrentPositionTime = 0;
    }

    public void updateProgress(int position, int duration) {
        if (isSeekByUser) {
            return;
        }
        mProgressView.setMax(duration);
        mProgressView.setProgress(position);
        mProgressView.setSecondaryProgress(position + mSecProcess);
        updateTime(position, duration);
    }

    public void setMediaType(ResourceType type) {
        this.mMediaType = type;
        hideDefaultControlView();
    }

    public void setResourceDefinition(List<ResourceDefinition> resources) {
        Map<String, ResourceDefinition> definitionMap = new LinkedHashMap<>();
        for (ResourceDefinition definition : resources) {
            definitionMap.put(definition.name(), definition);
        }
        this.mResourceDefinitionList = definitionMap;
        if (mResourceDefinitionList != null && !mResourceDefinitionList.isEmpty()) {
            if (TextUtils.isEmpty(mCurrentDefinitionName)) {
                mCurrentDefinitionName = resources.get(0).name();
            }
            updateDefinitionListView(mCurrentDefinitionName);
        }
    }

    public void setDefaultCurrentDefinitionName(String mCurrentDefinitionName) {
        this.mCurrentDefinitionName = mCurrentDefinitionName;
    }

    public void setControllerOptions(ControllerOptions controllerOptions) {
        this.mControllerOptions = controllerOptions;
        renderViewByOptions();
    }

    public void setProgressByPlayStatus(boolean play) {
        if (play) {
            if (getPlaybackState() == PlayerState.STATE_ENDED) {
                resetCurrentPositionTime();
            }
            updateMediaProgress();
        } else {
            pauseMediaProgress();
        }
    }

    public void setControllerListener(PlayerControlListener controllerListener) {
        this.mPlayerControllerListener = controllerListener;
    }

    public PlayerControlListener getControllerListener() {
        if (mPlayerControllerListener == null) {
            mPlayerControllerListener = new PlayerControlListener() {
            };
        }
        return mPlayerControllerListener;
    }

    public void setPlayerView(ResourcePlayer view) {
        this.mPlayerView = view;
    }

    public ResourcePlayer getPlayer() {
        if (mPlayerView == null) {
            throw new RuntimeException("PlayerView can not be null!");
        }
        return mPlayerView;
    }

    private long getPlayerDuration() {
        return getPlayer().getDuration();
    }

    public void setPlayWhenReady(boolean mPlayWhenReady) {
        this.mPlayWhenReady = mPlayWhenReady;
    }

    public void setPlaybackState(int mPlaybackState) {
        this.mPlaybackState = mPlaybackState;
    }

    public boolean getPlayWhenReady() {
        return mPlayWhenReady;
    }

    public int getPlaybackState() {
        return mPlaybackState;
    }

    public void setToolbarTitle(String title) {
        if (mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        }
    }

    private void onTouchEventBySelf(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mToolsView.getVisibility() == VISIBLE) {
                hideOverlay();
            } else {
                showOverlay(DEFAULT_TIMEOUT_COUNT);
            }
        }
    }

    private void hideDefaultControlView() {
        if (mMediaType == ResourceType.VIDEO) {
            return;
        }
        if (isDocControlView()) {
            hideDocControlView();
            return;
        }
        mScreenChangeView.setVisibility(GONE);
        ((View) mRateView.getParent()).setVisibility(VISIBLE);
    }

    private boolean isDocControlView() {
        return mMediaType == ResourceType.DOCUMENT || mMediaType == ResourceType.PPT;
    }

    private void hideDocControlView() {
        mToolsView.setVisibility(GONE);
        mToolbarView.setVisibility(GONE);
        mDocToolsView.setVisibility(VISIBLE);

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        setLayoutParams(lp);
    }

    private void renderViewByOptions() {
        if (mMediaType == ResourceType.VIDEO) {
            boolean isShowRate =
                    mControllerOptions.getOption(ControllerOptions.RATE) & mOrientation == Configuration.ORIENTATION_LANDSCAPE;
            ((View) mRateView.getParent()).setVisibility(isShowRate ? VISIBLE : GONE);
            mScreenChangeView.setVisibility(mControllerOptions.getOption(ControllerOptions.SCREEN) ? VISIBLE : GONE);
        }
        mProgressView.setEnabled(mControllerOptions.getOption(ControllerOptions.SEEK, true));
    }

    private void showOverlay(int overlayTimeout) {
        mToolsView.setVisibility(VISIBLE);
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mToolbarView.setVisibility(VISIBLE);
        }
        if (getBackground() == null) {
            setBackgroundResource(R.drawable.video_controller_view_bg);
        }
        getBackground().setAlpha(128);
        mHandler.removeMessages(FADE_OUT);
        if (overlayTimeout > 0) {
            hideOverlayDelayed(overlayTimeout);
        }
        getControllerListener().onChangeOverlay(true);
    }

    private void hideOverlayDelayed(int overlayTimeout) {
        mHandler.removeMessages(FADE_OUT);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), overlayTimeout);
    }

    private void hideOverlay() {
        if (getBackground() == null) {
            setBackgroundResource(R.drawable.video_controller_view_bg);
        }
        getBackground().setAlpha(0);
        mToolsView.setVisibility(INVISIBLE);
        mToolbarView.setVisibility(INVISIBLE);
        mHandler.removeMessages(FADE_OUT);
        getControllerListener().onChangeOverlay(false);
        hidePopWindows();
    }

    private void postDelayedProgressByCurrentRateIndex() {
        long delayMs = 1000;
        mCurrentPositionTime += delayMs / 1000;
        delayMs = (long) (delayMs / defaultRateArray[mCurrentRateIndex]);
        updatePlayStatus(true);
        postDelayed(updateProgressAction, delayMs);
    }

    public int getCurrentPositionTime() {
        mCurrentPositionTime = (int) getPlayer().getCurrentPosition();
        if (getPlayerDuration() <= 0 || mCurrentPositionTime < 0) {
            mCurrentPositionTime = 0;
        }
        return mCurrentPositionTime;
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };
}
