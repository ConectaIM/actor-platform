/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.viewmodel;

import im.actor.core.modules.Modules;
import im.actor.runtime.mvvm.AsyncVM;

/**
 * Value Model handler for Uploading File.
 * <p></p>
 * Create by calling method in Messenger object and ALWAYS release by calling detach method.
 */
public class CompressVideoVM extends AsyncVM {
    private long rid;
    private Modules modules;
    private CompressVideoVMCallback vmCallback;
    private CompressVideoCallback callback;

    /**
     * <p>INTERNAL API</p>
     * Create UploadFileVM
     *
     * @param rid        file random id
     * @param vmCallback file value model callback
     * @param modules    im.actor.android.modules reference
     */
    public CompressVideoVM(long rid, CompressVideoVMCallback vmCallback, Modules modules) {
        this.rid = rid;
        this.modules = modules;
        this.vmCallback = vmCallback;
        this.callback = new CompressVideoCallback() {

            @Override
            public void onNotConpressing() {
                post(new NotCompressing());
            }

            @Override
            public void onCompressing(float progress) {
                post(new Compressing(progress));
            }

            @Override
            public void onCompressed() {
                post(new Compressed());
            }
        };
        modules.getFilesModule().bindCompressVideo(rid, callback);
    }

    @Override
    protected void onObjectReceived(Object obj) {
        if (obj instanceof NotCompressing) {
            vmCallback.onNotConpressing();
        } else if (obj instanceof Compressing) {
            vmCallback.onCompressing(((Compressing) obj).getProgress());
        } else if (obj instanceof Compressed) {
            vmCallback.onCompressed();
        }
    }

    /**
     * Detach UploadFileVM from Messenger.
     * Don't use object after detaching.
     */
    @Override
    public void detach() {
        super.detach();
        modules.getFilesModule().unbindCompressVideo(rid, callback);
    }

    private class NotCompressing {

    }

    private class Compressing {
        private float progress;

        private Compressing(float progress) {
            this.progress = progress;
        }

        public float getProgress() {
            return progress;
        }
    }

    private class Compressed {

    }
}
