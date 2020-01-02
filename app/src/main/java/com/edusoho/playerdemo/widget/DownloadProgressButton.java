package com.edusoho.playerdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.edusoho.cloud.manager.DownloadInfo;
import com.edusoho.playerdemo.R;

public class DownloadProgressButton extends View {
    //进度最大值
    private int                 max;
    //当前进度
    private int                 progress;
    //当前模式
    private DownloadInfo.Status downloadStatus = DownloadInfo.Status.NONE;
    //进度条的颜色
    private int                 progressColor;
    //进度条宽度百分比
    private int                 progressWidthPercent;
    //底层背景色
    private int                 bottomColor;
    //最上层圆的颜色
    private int                 topColor;
    //进度条画笔
    private Paint               progressPaint;
    //当前模式
    private Context             mContext;


    private boolean isPause      = false;
    private boolean isButtonLock = false;

    public DownloadProgressButton(Context context) {
        this(context, null);
        mContext = context;
    }

    public DownloadProgressButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public DownloadProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initDefault();
        initView();
    }

    private void initView() {
        //进度条画笔
        progressPaint = new Paint();
        //设置抗锯齿
        progressPaint.setAntiAlias(true);
        //初始化textRect
    }

    /**
     * 设置默认值
     */
    private void initDefault() {
        //最上层默认为透明
        topColor = Color.TRANSPARENT;
        //进度条默认为绿色
        progressColor = getResources().getColor(R.color.primary_color);
        //背景为灰色
        bottomColor = getResources().getColor(R.color.white);
        //进度0
        progress = 0;
        //最大进度100
        max = 100;
        //进度条宽度比例50
        progressWidthPercent = 50;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressColor = getResources().getColor(R.color.primary_color);

        switch (downloadStatus) {
            case PENDING:
            case RUNNING:
                setBackground(getResources().getDrawable(R.drawable.icon_download));
                drawCircle(canvas);
                break;
            case IDLE:
                setBackground(getResources().getDrawable(R.drawable.icon_download_pause));
                progressColor = getResources().getColor(R.color.primary_red_color);
                drawCircle(canvas);
                break;
            case COMPLETED:
                setBackground(getResources().getDrawable(R.drawable.icon_download_success));
                break;
            case NONE:
                setBackground(getResources().getDrawable(R.drawable.icon_download));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + downloadStatus);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawCircle(Canvas canvas) {
        int width  = getWidth();
        int height = getHeight();
        //中点
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        //获取宽高中较短的作为底层圆的半径
        int bottomRadius = width > height ? getHeight() / 2 : getWidth() / 2;
        //进度条宽度占的比例
        double scale = progressWidthPercent / 100.00;
        //这四个参数用来drawText以及drawArc
        int top    = centerY - bottomRadius;
        int bottom = centerY + bottomRadius;
        int left   = centerX - bottomRadius;
        int right  = centerX + bottomRadius;
        //显示的进度
        if (max <= 0) {
            max = 100;
        }
        if (progress <= 0) {
            progress = 1;
        }
        int showProgress = progress * 100 / max;
        //绘制最底层的大圆(空心圆)
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(bottomColor);
        //大圆的宽度刚好等于进度条的宽度
        int strokeWidth = (int) (bottomRadius * scale) / 2;
        progressPaint.setStrokeWidth(strokeWidth);
        //这里要考虑到strokeWidth的长度
        canvas.drawCircle(centerX, centerY, bottomRadius - strokeWidth / 2, progressPaint);
        //根据进度绘制进度条
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(progressColor);
        //起始角度为-90度才是从正上方开始画
        canvas.drawArc(left + strokeWidth / 2, top + strokeWidth / 2, right - strokeWidth / 2, bottom - strokeWidth / 2,
                -90, 360 * showProgress / 100, false, progressPaint);
        //绘制最上层的圆(实心圆)
        progressPaint.setStyle(Paint.Style.FILL);
        int topRadius = (int) (bottomRadius * (1 - scale));
        progressPaint.setColor(topColor);
        //设置边框宽度
        progressPaint.setStrokeWidth(0);
        canvas.drawCircle(centerX, centerY, topRadius, progressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode  = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize  = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //计算View的宽高
        int width = widthSize, height = heightSize;
        if (widthMode == MeasureSpec.EXACTLY) {
            //指定大小或者match_parent
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //wrap_content
            width = 100;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            //指定大小或者match_parent
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //wrap_content
            height = 100;
        }
        setMeasuredDimension(width, height);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    public void setDownloadStatus(DownloadInfo.Status downloadStatus) {
        if (isButtonLock() && downloadStatus != DownloadInfo.Status.PENDING) {
            return;
        }
        this.downloadStatus = downloadStatus;
        postInvalidate();
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isPause() {
        return isPause;
    }

    /**
     * 点击下载按钮，按钮背景改为暂停状态，然后锁住按钮状态，直到按钮被再次点击继续下载，释放锁子
     * @param pause
     */
    public void setPause(boolean pause) {
        if (!pause) {
            setDownloadStatus(DownloadInfo.Status.IDLE);
        }
        setButtonLock(!pause);
        isPause = pause;
    }

    public boolean isButtonLock() {
        return isButtonLock;
    }

    public void setButtonLock(boolean buttonLock) {
        isButtonLock = buttonLock;
    }
}
