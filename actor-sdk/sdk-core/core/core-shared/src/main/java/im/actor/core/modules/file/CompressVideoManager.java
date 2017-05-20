package im.actor.core.modules.file;


import im.actor.core.modules.ModuleActor;
import java.util.ArrayList;
import java.util.HashMap;
import im.actor.core.modules.ModuleContext;
import im.actor.core.viewmodel.CompressVideoCallback;
import im.actor.core.viewmodel.UploadFileCallback;

import im.actor.runtime.VideoCompressorRuntimeProvider;
import im.actor.runtime.actors.ActorRef;


/**
 * Created by diego on 14/05/17.
 */

public class CompressVideoManager extends ModuleActor {


    private VideoCompressorRuntimeProvider videoCompressorRuntime = new VideoCompressorRuntimeProvider();
    private HashMap<Long, ArrayList<CompressVideoCallback>> callbacks = new HashMap<>();
    private ArrayList<QueueItem> queue = new ArrayList<>();

    public CompressVideoManager(ModuleContext context) {
        super(context);
    }

    public void startCompression(long rid, final String fileName, final String originalVideoPath) {
        ActorRef sender = sender();
        videoCompressorRuntime.compressVideo(originalVideoPath).then((cv)->{
            sender.send(new CompressionCompleted(rid, originalVideoPath, fileName));
        }).failure((ex)->{
            sender.send(new CompressionFailed(rid, originalVideoPath, fileName));
        });
    }

    public void bindUpload(long rid, final CompressVideoCallback callback) {

        QueueItem queueItem = findItem(rid);
        if (queueItem == null) {
            im.actor.runtime.Runtime.dispatch(() -> callback.onNotConpressing());
        } else {
            if (queueItem.isStopped) {
                im.actor.runtime.Runtime.dispatch(() -> callback.onNotConpressing());
            } else {
                final float progress = queueItem.progress;
                im.actor.runtime.Runtime.dispatch(() -> callback.onCompressing(progress));
            }
        }

        ArrayList<CompressVideoCallback> clist = callbacks.get(rid);
        if (clist == null) {
            clist = new ArrayList<>();
            callbacks.put(rid, clist);
        }
        clist.add(callback);
    }

    public void unbindUpload(long rid, UploadFileCallback callback) {
        ArrayList<CompressVideoCallback> clist = callbacks.get(rid);
        if (clist != null) {
            clist.remove(callback);
        }
    }

    private QueueItem findItem(long rid) {
        for (QueueItem q : queue) {
            if (q.rid == rid) {
                return q;
            }
        }
        return null;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof StartCompression) {
            StartCompression startCompression = (StartCompression) message;
            startCompression(startCompression.getRid(), startCompression.getFileName(), startCompression.getOriginalVideoPath());
        } else {
            super.onReceive(message);
        }
    }

    public static class StartCompression {
        private long rid;
        private String fileName;
        private String originalVideoPath;

        public StartCompression(long rid, String fileName, String originalVideoPath) {
            this.rid = rid;
            this.fileName = fileName;
            this.originalVideoPath = originalVideoPath;
        }

        public long getRid() {
            return rid;
        }

        public String getOriginalVideoPath() {
            return originalVideoPath;
        }

        public String getFileName() {
            return fileName;
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

    public static class BindCompress {
        private long rid;
        private CompressVideoCallback callback;

        public BindCompress(long rid, CompressVideoCallback callback) {
            this.rid = rid;
            this.callback = callback;
        }

        public long getRid() {
            return rid;
        }

        public CompressVideoCallback getCallback() {
            return callback;
        }
    }

    public static class UnbindCompress {
        private long rid;
        private CompressVideoCallback callback;

        public UnbindCompress(long rid, CompressVideoCallback callback) {
            this.rid = rid;
            this.callback = callback;
        }

        public long getRid() {
            return rid;
        }

        public CompressVideoCallback getCallback() {
            return callback;
        }
    }

    private class QueueItem {
        private long rid;
        private String fileDescriptor;
        private boolean isStopped;
        private boolean isStarted;
        private float progress;
        private ActorRef taskRef;
        private ActorRef requestActor;
        private String fileName;

        private QueueItem(long rid, String fileDescriptor, String fileName, ActorRef requestActor) {
            this.rid = rid;
            this.fileDescriptor = fileDescriptor;
            this.requestActor = requestActor;
            this.fileName = fileName;
        }
    }

}