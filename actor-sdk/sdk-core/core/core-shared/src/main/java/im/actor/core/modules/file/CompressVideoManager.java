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
import im.actor.runtime.Runtime;
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

    public void startCompression(long rid, final String fileName, final String originalVideoPath, final String compressedVideoPath, final boolean removeOriginal) {
        ActorRef sender = sender();
        videoCompressorRuntime.compressVideo(originalVideoPath, compressedVideoPath, removeOriginal).then((cv)->{
            sender.send(new CompressionCompleted(rid, originalVideoPath, fileName));
        }).failure((ex)->{
            sender.send(new CompressionFailed(rid, originalVideoPath, fileName));
        });
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof StartCompression) {
            StartCompression startCompression = (StartCompression) message;
            startCompression(startCompression.getRid(), startCompression.getFileName(), startCompression.getOriginalVideoPath(),
                    startCompression.getCompressedVideoPath(), startCompression.isRemoveOriginal());
        } else {
            super.onReceive(message);
        }
    }

    public static class StartCompression {
        private long rid;
        private String fileName;
        private String originalVideoPath;
        private String compressedVideoPath;
        private boolean removeOriginal;

        public StartCompression(long rid, String fileName, String originalVideoPath, String compressedVideoPath, boolean removeOriginal) {
            this.rid = rid;
            this.fileName = fileName;
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

        public String getFileName() {
            return fileName;
        }

        public boolean isRemoveOriginal() {
            return removeOriginal;
        }
    }

    public static class CompressionCompleted {
        private long rid;
        private String filePath;
        private String fileName;

        public CompressionCompleted(long rid, String filePath, String fileName) {
            this.rid = rid;
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public long getRid() {
            return rid;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public static class CompressionFailed {
        private long rid;
        private String filePath;
        private String fileName;

        public CompressionFailed(long rid, String filePath, String fileName) {
            this.rid = rid;
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public long getRid() {
            return rid;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileName() {
            return fileName;
        }
    }

}