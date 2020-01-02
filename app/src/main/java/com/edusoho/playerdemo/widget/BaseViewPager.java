package com.edusoho.playerdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class BaseViewPager extends ViewPager {
    private int preX = 0;

    public BaseViewPager(@NonNull Context context) {
        super(context);
    }

    public BaseViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            preX = (int) ev.getX();
        } else {
            if (Math.abs((int) ev.getX() - preX) > 10) {
                return true;
            } else {
                preX = (int) ev.getX();
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
