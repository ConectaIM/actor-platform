package im.actor.runtime;


import com.google.j2objc.annotations.ObjectiveCName;

import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.video.CompressedVideo;

/**
 * Created by diego on 23/05/17.
 */

public interface VideoCompressorRuntime {

    @ObjectiveCName("compressVideo:withOriginalPath:withSender:withCallback:")
    Promise<CompressedVideo> compressVideo(long rid, String originalPath, ActorRef sender, CompressorProgressListener progressCallback);
}


