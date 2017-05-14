package im.actor.core.entity;

/**
 * Created by diego on 14/05/17.
 */

public class CompressedVideo {

    private String fileName;
    private String filePath;

    public CompressedVideo(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
