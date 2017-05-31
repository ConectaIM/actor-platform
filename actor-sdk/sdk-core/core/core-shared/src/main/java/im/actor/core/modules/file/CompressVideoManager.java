package im.actor.core.modules.file;


import java.util.ArrayList;
import java.util.HashMap;

import im.actor.core.modules.ModuleActor;
import im.actor.core.modules.ModuleContext;
import im.actor.core.viewmodel.CompressVideoCallback;
import im.actor.runtime.CompressorProgressListener;
import im.actor.runtime.Storage;
import im.actor.runtime.actors.ActorRef;


/**
 * Created by diego on 14/05/17.
 */

public class CompressVideoManager extends ModuleActor {


    private HashMap<Long, ArrayList<CompressVideoCallback>> callbacks = new HashMap<>();
    private ArrayList<CompressItem> queue = new ArrayList<>();

    public CompressVideoManager(ModuleContext context) {
        super(context);
    }

    public void startCompression(long rid, final String fileName, final String originalVideoPath) {

        CompressItem oldItem = findItem(rid);
        if (oldItem != null) {
            queue.remove(oldItem);
        }

        final CompressItem ci = new CompressItem(rid, fileName, originalVideoPath);
        ci.sender = sender();
        ci.isStarted = true;
        queue.add(ci);


        Storage.getVideoCompressorRuntime().compressVideo(ci.getRid(), ci.getOriginalFilePath(), ci.getSender(), new CompressorProgressListener() {
            @Override
            public void onProgress(long rid, float v) {
                ArrayList<CompressVideoCallback> clist = callbacks.get(rid);
                if (clist != null) {
                    for (final CompressVideoCallback callback : clist) {
                        im.actor.runtime.Runtime.dispatch(() -> callback.onCompressing(v));
                    }
                }
            }
        }).then((cv) -> {
            ci.isStarted = false;
            queue.remove(ci);

            ArrayList<CompressVideoCallback> clist = callbacks.get(rid);
            if (clist != null) {
                for (final CompressVideoCallback callback : clist) {
                    im.actor.runtime.Runtime.dispatch(() -> callback.onCompressed());
                }
            }
            cv.getSender().send(new CompressionCompleted(cv.getRid(), cv.getFilePath(), cv.getFileName()));
        }).failure((ex) -> {
            ci.isStarted = false;
            ArrayList<CompressVideoCallback> clist = callbacks.get(rid);

            if (clist != null) {
                for (final CompressVideoCallback callback : clist) {
                    im.actor.runtime.Runtime.dispatch(() -> callback.onNotConpressing());
                }
            }
            ci.getSender().send(new CompressionFailed(ci.getRid(), ci.getOriginalFilePath(), ci.getFileName()));
        });
    }

    public void bindCompress(long rid, final CompressVideoCallback callback) {

        CompressItem queueItem = findItem(rid);

        if (queueItem == null) {
            im.actor.runtime.Runtime.dispatch(() -> callback.onNotConpressing());
        } else {
            final float progress = queueItem.progress;
            im.actor.runtime.Runtime.dispatch(() -> callback.onCompressing(progress));
        }

        ArrayList<CompressVideoCallback> clist = callbacks.get(rid);

        if (clist == null) {
            clist = new ArrayList<>();
            callbacks.put(rid, clist);
        }

        clist.add(callback);
    }

    public void unbindCompress(long rid, CompressVideoCallback callback) {
        ArrayList<CompressVideoCallback> clist = callbacks.get(rid);
        if (clist != null) {
            clist.remove(callback);
        }
    }

    public void requestState(long rid, final CompressVideoCallback callback) {
        CompressItem queueItem = findItem(rid);
        if (queueItem == null) {
            im.actor.runtime.Runtime.dispatch(() -> callback.onNotConpressing());
        } else {
            final float progress = queueItem.progress;
            im.actor.runtime.Runtime.dispatch(() -> callback.onCompressing(progress));
        }
    }

    public void resumeCompressing(long rid) {
        CompressItem queueItem = findItem(rid);
        if (queueItem != null) {
            if (queueItem.isStarted) {
                return;
            }

            queueItem.progress = 0;

            ArrayList<CompressVideoCallback> clist = callbacks.get(rid);
            if (clist != null) {
                for (final CompressVideoCallback callback : clist) {
                    im.actor.runtime.Runtime.dispatch(() -> callback.onCompressing(0));
                }
            }

            startCompression(queueItem.getRid(), queueItem.fileName, queueItem.originalFilePath);
        }
    }

    private CompressItem findItem(long rid) {
        for (CompressItem q : queue) {
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
        } else if (message instanceof BindCompress) {
            BindCompress bindCompress = (BindCompress) message;
            bindCompress(bindCompress.getRid(), bindCompress.callback);
        } else if (message instanceof UnbindCompress) {
            UnbindCompress unbindCompress = (UnbindCompress) message;
            unbindCompress(unbindCompress.getRid(), unbindCompress.callback);
        } else if (message instanceof RequestState) {
            RequestState requestState = (RequestState) message;
            requestState(requestState.getRid(), requestState.callback);
        } else if (message instanceof ResumeCompressing) {
            ResumeCompressing resumeCompressing = (ResumeCompressing) message;
            resumeCompressing(resumeCompressing.getRid());
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

    public static class RequestState {
        private long rid;
        private CompressVideoCallback callback;

        public RequestState(long rid, CompressVideoCallback callback) {
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

    public static class ResumeCompressing {
        private long rid;

        public ResumeCompressing(long rid) {
            this.rid = rid;
        }

        public long getRid() {
            return rid;
        }
    }

    public class CompressItem {
        private long rid;
        private String fileName;
        private String originalFilePath;
        private float progress;

        private ActorRef sender;

        private boolean isStarted;

        private CompressItem(long rid, String fileName, String originalFilePath) {
            this.rid = rid;
            this.fileName = fileName;
            this.originalFilePath = originalFilePath;
        }

        public long getRid() {
            return rid;
        }

        public String getFileName() {
            return fileName;
        }

        public String getOriginalFilePath() {
            return originalFilePath;
        }

        public ActorRef getSender() {
            return sender;
        }
    }

}