package im.actor.runtime;

/**
 * Created by diego on 23/05/17.
 */

public interface CompressorProgressListener {
    void onProgress(long rid, float v);
}
