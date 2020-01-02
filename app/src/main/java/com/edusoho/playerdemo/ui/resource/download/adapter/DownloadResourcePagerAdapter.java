package com.edusoho.playerdemo.ui.resource.download.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class DownloadResourcePagerAdapter extends FragmentPagerAdapter {
    private Context                        mContext;
    private String[]                       mTitles;
    private Fragment[]                     mLists;
    private List<DownloadFragmentListener> downloadFragmentListener;

    public DownloadResourcePagerAdapter(Context context, FragmentManager fm, String[] titles, Fragment[] list, List<DownloadFragmentListener> downloadFragmentListener) {
        super(fm);
        mContext = context;
        mTitles = titles;
        mLists = list;
        this.downloadFragmentListener = downloadFragmentListener;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = mLists[i];
        downloadFragmentListener.add(((DownloadFragmentListener) fragment));
        return fragment;
    }

    @Override
    public int getCount() {
        return mLists.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
