package im.actor.core.modules.file;

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
import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.actors.Props;
import im.actor.runtime.actors.messages.PoisonPill;
import im.actor.runtime.files.FileSystemReference;

/**
 * Created by diego on 14/05/17.
 */

public class CompressVideoManager extends ModuleActor {


    public CompressVideoManager(ModuleContext context) {
        super(context);
    }

    // Tasks

    public void startUpload(long rid, String descriptor, String fileName, ActorRef requestActor) {

    }

    public void stopUpload(long rid) {

    }

    public void bindUpload(long rid, final UploadFileCallback callback) {

    }

    public void unbindUpload(long rid, UploadFileCallback callback) {

    }

    public void requestState(long rid, final UploadFileCallback callback) {

    }

    public void onUploadTaskError(long rid) {

    }



    public void onUploadTaskComplete(long rid, FileReference fileReference, FileSystemReference reference) {

    }

    private void checkQueue() {

    }

    private QueueItem findItem(long rid) {
        for (QueueItem q : queue) {
            if (q.rid == rid) {
                return q;
            }
        }
        return null;
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

    //region Messages

    @Override
    public void onReceive(Object message) {
        if (message instanceof StartUpload) {
            StartUpload startUpload = (StartUpload) message;
            startUpload(startUpload.getRid(), startUpload.getFileDescriptor(),
                    startUpload.getFileName(), sender());
        } else if (message instanceof StopUpload) {
            StopUpload cancelUpload = (StopUpload) message;
            stopUpload(cancelUpload.getRid());
        } else if (message instanceof UploadTaskError) {
            UploadTaskError uploadTaskError = (UploadTaskError) message;
            onUploadTaskError(uploadTaskError.getRid());
        } else if (message instanceof UploadTaskProgress) {
            UploadTaskProgress taskProgress = (UploadTaskProgress) message;
            onUploadTaskProgress(taskProgress.getRid(), taskProgress.getProgress());
        } else if (message instanceof UploadTaskComplete) {
            UploadTaskComplete taskComplete = (UploadTaskComplete) message;
            onUploadTaskComplete(taskComplete.getRid(), taskComplete.getLocation(),
                    taskComplete.getReference());
        } else if (message instanceof BindUpload) {
            BindUpload bindUpload = (BindUpload) message;
            bindUpload(bindUpload.getRid(), bindUpload.getCallback());
        } else if (message instanceof UnbindUpload) {
            UnbindUpload unbindUpload = (UnbindUpload) message;
            unbindUpload(unbindUpload.getRid(), unbindUpload.getCallback());
        } else if (message instanceof RequestState) {
            RequestState requestState = (RequestState) message;
            requestState(requestState.getRid(), requestState.getCallback());
        } else if (message instanceof PauseUpload) {
            PauseUpload pauseUpload = (PauseUpload) message;
            pauseUpload(pauseUpload.getRid());
        } else if (message instanceof ResumeUpload) {
            ResumeUpload resumeUpload = (ResumeUpload) message;
            resumeUpload(resumeUpload.getRid());
        } else {
            super.onReceive(message);
        }
    }

    public static class StartUpload {
        private long rid;
        private String fileDescriptor;
        private String fileName;

        public StartUpload(long rid, String fileDescriptor, String fileName) {
            this.rid = rid;
            this.fileDescriptor = fileDescriptor;
            this.fileName = fileName;
        }

        public long getRid() {
            return rid;
        }

        public String getFileDescriptor() {
            return fileDescriptor;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public static class BindUpload {
        private long rid;
        private UploadFileCallback callback;

        public BindUpload(long rid, UploadFileCallback callback) {
            this.rid = rid;
            this.callback = callback;
        }

        public long getRid() {
            return rid;
        }

        public UploadFileCallback getCallback() {
            return callback;
        }
    }

    public static class UnbindUpload {
        private long rid;
        private UploadFileCallback callback;

        public UnbindUpload(long rid, UploadFileCallback callback) {
            this.rid = rid;
            this.callback = callback;
        }

        public long getRid() {
            return rid;
        }

        public UploadFileCallback getCallback() {
            return callback;
        }
    }

    public static class StopUpload {
        private long rid;

        public StopUpload(long rid) {
            this.rid = rid;
        }

        public long getRid() {
            return rid;
        }
    }

    public static class UploadTaskError {
        private long rid;

        public UploadTaskError(long rid) {
            this.rid = rid;
        }

        public long getRid() {
            return rid;
        }
    }

    public static class UploadTaskProgress {
        private long rid;
        private float progress;

        public UploadTaskProgress(long rid, float progress) {
            this.rid = rid;
            this.progress = progress;
        }

        public long getRid() {
            return rid;
        }

        public float getProgress() {
            return progress;
        }
    }

    public static class UploadTaskComplete {
        private long rid;
        private FileReference location;
        private FileSystemReference reference;

        public UploadTaskComplete(long rid, FileReference location, FileSystemReference reference) {
            this.rid = rid;
            this.location = location;
            this.reference = reference;
        }

        public long getRid() {
            return rid;
        }

        public FileSystemReference getReference() {
            return reference;
        }

        public FileReference getLocation() {
            return location;
        }
    }

    public static class UploadCompleted {
        private long rid;
        private FileReference fileReference;

        public UploadCompleted(long rid, FileReference fileReference) {
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

    public static class UploadError {
        private long rid;

        public UploadError(long rid) {
            this.rid = rid;
        }

        public long getRid() {
            return rid;
        }
    }

    public static class RequestState {
        private long rid;
        private UploadFileCallback callback;

        public RequestState(long rid, UploadFileCallback callback) {
            this.rid = rid;
            this.callback = callback;
        }

        public long getRid() {
            return rid;
        }

        public UploadFileCallback getCallback() {
            return callback;
        }
    }

    public static class PauseUpload {
        private long rid;

        public PauseUpload(long rid) {
            this.rid = rid;
        }

        public long getRid() {
            return rid;
        }
    }

    public static class ResumeUpload {
        private long rid;

        public ResumeUpload(long rid) {
            this.rid = rid;
        }

        public long getRid() {
            return rid;
        }
    }