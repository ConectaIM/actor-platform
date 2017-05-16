package im.actor.core.modules.file;

import im.actor.core.entity.CompressedVideo;
import im.actor.core.modules.ModuleActor;

import java.util.ArrayList;
import java.util.HashMap;

import im.actor.core.entity.FileReference;
import im.actor.core.modules.ModuleActor;
import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.file.entity.Downloaded;
import im.actor.core.util.RandomUtils;
import im.actor.core.viewmodel.UploadFileCallback;
import im.actor.runtime.Log;
import im.actor.runtime.VideoCompressorRuntimeProvider;
import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.actors.Props;
import im.actor.runtime.actors.messages.PoisonPill;
import im.actor.runtime.files.FileSystemReference;

/**
 * Created by diego on 14/05/17.
 */

public class CompressVideoManager extends ModuleActor {


    private VideoCompressorRuntimeProvider videoCompressorRuntime = new VideoCompressorRuntimeProvider();

    public CompressVideoManager(ModuleContext context) {
        super(context);
    }

    // Tasks

    public void startCompression(long rid, String originalVideoPath, String compressedVideoPath, boolean removeOriginal) {
        CompressedVideo cv = videoCompressorRuntime.compressVideo(originalVideoPath, compressedVideoPath, removeOriginal);

    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof StartCompression) {
            StartCompression startCompression = (StartCompression) message;
            startCompression(startCompression.getRid(), startCompression.getOriginalVideoPath(),
                    startCompression.getCompressedVideoPath(), startCompression.isRemoveOriginal());
        } else {
            super.onReceive(message);
        }
    }

    public static class StartCompression {
        private long rid;
        private String originalVideoPath;
        private String compressedVideoPath;
        private boolean removeOriginal;

        public StartCompression(long rid, String originalVideoPath, String compressedVideoPath, boolean removeOriginal) {
            this.rid = rid;
            this.originalVideoPath = originalVideoPath;
            this.compressedVideoPath = compressedVideoPath;
            this.removeOriginal = removeOriginal;
        }

        public long getRid() {
            return rid;
        }

        public String getOriginalVideoPath() {
            return originalVideoPath;
        }

        public String getCompressedVideoPath() {
            return compressedVideoPath;
        }

        public boolean isRemoveOriginal() {
            return removeOriginal;
        }
    }


    public static class CompressionCompleted {
        private long rid;
        private FileReference fileReference;

        public CompressionCompleted(long rid, FileReference fileReference) {
            this.rid = rid;
            this.fileReference = fileReference;
        }

        public long getRid() {
            return rid;
        }

        public FileReference getFileReference() {
            return fileReference;
        }
    }

    public static class CompressionFailed {
        private long rid;
        private FileReference fileReference;

        public CompressionFailed(long rid, FileReference fileReference) {
            this.rid = rid;
            this.fileReference = fileReference;
        }

        public long getRid() {
            return rid;
        }

        public FileReference getFileReference() {
            return fileReference;
        }
    }

}