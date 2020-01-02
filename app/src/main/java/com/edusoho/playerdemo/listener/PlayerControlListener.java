package com.edusoho.playerdemo.listener;

import com.edusoho.cloud.core.entity.ResourceDefinition;

public interface PlayerControlListener {
    default void onSeek(int position) {
    }

    default void onChangeScreen(int orientation) {
    }

    default void onPlayStatusChange(boolean isPlay) {
    }

    default void onChangeRate(float rate) {
    }

    default void onChangePlaySource(ResourceDefinition definition) {
    }

    default void onChangeOverlay(boolean isShow) {
    }

    default void onChangePage(boolean isPrev) {
    }
}
