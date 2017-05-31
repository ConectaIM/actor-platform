package im.actor.runtime.cocoa;

import com.google.j2objc.annotations.ObjectiveCName;

import im.actor.runtime.CompressorProgressListener;
import im.actor.runtime.VideoCompressorRuntime;
import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.video.CompressedVideo;


public class CocoaVideoCompressorProxyProvider implements VideoCompressorRuntime {

    private static VideoCompressorRuntime runtime;

    @ObjectiveCName("setVideoCompressorRuntime:")
    public static void setVideoCompressorRuntime(VideoCompressorRuntime runtime) {
        CocoaVideoCompressorProxyProvider.runtime = runtime;
    }

    @Override
    public Promise<CompressedVideo> compressVideo(long rid, String originalPath, ActorRef sender, CompressorProgressListener progressCallback) {
        return runtime.compressVideo(rid, originalPath, sender, progressCallback);
    }
}
