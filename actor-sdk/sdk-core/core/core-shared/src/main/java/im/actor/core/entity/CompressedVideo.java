package im.actor.core.entity;

import im.actor.runtime.actors.ActorRef;

/**
 * Created by diego on 14/05/17.
 */

public class CompressedVideo {

    private long rid;
    private String fileName;
    private String filePath;
    private ActorRef sender;

    public CompressedVideo(long rid, String fileName, String filePath, ActorRef sender) {
        this.rid = rid;
        this.fileName = fileName;
        this.filePath = filePath;
        this.sender = sender;
    }

    public long getRid() {
        return rid;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public ActorRef getSender() {
        return sender;
    }
}
