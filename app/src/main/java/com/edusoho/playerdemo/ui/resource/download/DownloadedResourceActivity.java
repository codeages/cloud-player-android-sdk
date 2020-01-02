package com.edusoho.playerdemo.ui.resource.download;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.extensions.PagerSlidingTabStrip;
import com.edusoho.playerdemo.ui.resource.download.adapter.DownloadFragmentListener;
import com.edusoho.playerdemo.ui.resource.download.adapter.DownloadResourcePagerAdapter;
import com.edusoho.playerdemo.ui.resource.download.fragment.DownloadedFragment;
import com.edusoho.playerdemo.ui.resource.download.fragment.DownloadingFragment;
import com.edusoho.playerdemo.ui.resource.base.BaseResourceActivity;
import com.edusoho.playerdemo.widget.BaseViewPager;

import java.util.ArrayList;
import java.util.List;

public class DownloadedResourceActivity extends BaseResourceActivity {
    public              Fragment[] DOWNLOAD_FRAGMENTS;
    public static final String[]   DOWNLOAD_TITLES = {"已下载", "下载中"};
    private             Drawable   oldBackground   = null;
    private             int        currentColor    = R.color.primary_color;
    private             boolean    isEdit          = false;

    PagerSlidingTabStrip mPagerTab;
    BaseViewPager        mViewPagers;
    private List<DownloadFragmentListener> mDownloadFragmentListener = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewPager();
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_uploaded_resource;
    }

    @Override
    public void initView() {
        super.initView();
        mPagerTab = findViewById(R.id.tab_download);
        mViewPagers = findViewById(R.id.viewpager_download);

        DownloadedFragment  downloaded  = new DownloadedFragment();
        DownloadingFragment downloading = new DownloadingFragment();
        DOWNLOAD_FRAGMENTS = new Fragment[]{downloaded, downloading};

        initBackBtn();
        setTitle(R.string.downloaded_resource);
        setRightBtnEvent(getString(R.string.dialog_edit_title), v -> {
            if (downloaded.isSelected()) {
                switchEditStatus(isEdit, downloaded);
            }

            if (downloading.isSelected()) {
                switchEditStatus(isEdit, downloading);
            }
        });
    }

    private void switchEditStatus(boolean b, DownloadFragmentListener listener) {
        if (!b) {
            isEdit = true;
            listener.switchSelectStatus(true);
            changeEditButtonText(isEdit);
        } else {
            isEdit = false;
            listener.switchSelectStatus(false);
            changeEditButtonText(isEdit);
        }
    }

    private void changeEditButtonText(boolean isEdit) {
        if (isEdit) {
            mBtnRight.setText(getString(R.string.cancel));
        } else {
            mBtnRight.setText(getString(R.string.edit));
        }
    }

    @Override
    public void initData() {
        super.initData();
    }

    private void initViewPager() {
        DownloadResourcePagerAdapter myPagerAdapter = new DownloadResourcePagerAdapter(this, getSupportFragmentManager(), DOWNLOAD_TITLES, DOWNLOAD_FRAGMENTS, mDownloadFragmentListener);
        mViewPagers.setAdapter(myPagerAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPagers.setPageMargin(pageMargin);
        mViewPagers.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for (int i = 0; i < fragmentList.size(); i++) {
                    Fragment fragment = fragmentList.get(i);
                    if (fragment instanceof DownloadFragmentListener) {
                        ((DownloadFragmentListener) fragment).onSelected(position == i);
                        if (position == i) {
                            changeEditButtonText(!((DownloadFragmentListener) fragment).isEditStatus());
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mPagerTab.setViewPager(mViewPagers);
        changeColor(currentColor);
        mViewPagers.setCurrentItem(0);
        mViewPagers.setOffscreenPageLimit(DOWNLOAD_FRAGMENTS.length);
    }

    private void changeColor(int newColor) {
        mPagerTab.setIndicatorColor(newColor);

        Drawable      colorDrawable  = new ColorDrawable(newColor);
        Drawable      bottomDrawable = new ColorDrawable(0);
        LayerDrawable ld             = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});

        if (oldBackground != null) {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }
}
