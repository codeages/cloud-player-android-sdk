package com.edusoho.playerdemo.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.edusoho.cloud.player.entity.WatermarkLocation;
import com.edusoho.playerdemo.R;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SampleAdapter extends BaseExpandableListAdapter {

    private List<SampleGroup> sampleGroups;
    private Activity          mActivity;

    public SampleAdapter(Activity activity) {
        sampleGroups = Collections.emptyList();
        this.mActivity = activity;
    }

    public void setSampleGroups(SampleGroup sampleGroups) {
        this.sampleGroups = Collections.singletonList(sampleGroups);
        notifyDataSetChanged();
    }

    @Override
    public Sample getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).samples.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(R.layout.sample_list_item, parent, false);
        }
        initializeChildView(view, getChild(groupPosition, childPosition));
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).samples.size();
    }

    @Override
    public SampleGroup getGroup(int groupPosition) {
        return sampleGroups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        ((TextView) view).setText(getGroup(groupPosition).title);
        return view;
    }

    @Override
    public int getGroupCount() {
        return sampleGroups.size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void initializeChildView(View view, Sample sample) {
        view.setTag(sample);
        TextView sampleTitle = view.findViewById(R.id.sample_title);
        sampleTitle.setText(sample.name);
    }

    public static final class SampleGroup {
        final String       title;
        final List<Sample> samples;

        public SampleGroup(String title, List<Sample> samples) {
            this.title = title;
            this.samples = samples;
        }
    }

    public static class Sample implements Serializable {
        public final String name;
        public final String resNo;
        public final String token;
        public final String watermarkLocation;
        public final String fingerprint;

        public Sample(String name, String resNo, String token, String watermarkLocation, String fingerprint) {
            this.name = name;
            this.resNo = resNo;
            this.token = token;
            this.watermarkLocation = watermarkLocation;
            this.fingerprint = fingerprint;
        }

        public String getFingerprint() {
            return this.fingerprint;
        }

        public WatermarkLocation getWatermarkLocation() {
            if (watermarkLocation.isEmpty()) {
                return null;
            }
            return Arrays.asList(WatermarkLocation.values()).get(Integer.parseInt(watermarkLocation));
        }
    }
}
