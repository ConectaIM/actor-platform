/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.viewmodel;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Upload file callback. Methods always called on background thread.
 */
public interface CompressVideoCallback {
    /**
     * On File not compressing
     */
    @ObjectiveCName("onNotConpressing")
    void onNotConpressing();

    /**
     * On File compressing in progress
     *
     * @param progress progress value in [0..1]
     */
    @ObjectiveCName("onCompressing:")
    void onCompressing(float progress);

    /**
     * On file compressed
     */
    @ObjectiveCName("onCompressed")
    void onCompressed();
}
