package im.actor.runtime;


import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.video.CompressedVideo;

/**
 * Created by diego on 13/05/17.
 */

public class VideoCompressorRuntimeProvider implements VideoCompressorRuntime {
    @Override
    public Promise<CompressedVideo> compressVideo(long rid, String originalPath, ActorRef sender, CompressorProgressListener progressCallback) {
        throw new RuntimeException("Dumb");
    }
}
