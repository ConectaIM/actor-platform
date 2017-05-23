package im.actor.runtime;

import im.actor.core.entity.CompressedVideo;
import im.actor.core.modules.file.CompressVideoManager;
import im.actor.runtime.promise.Promise;

/**
 * Created by diego on 23/05/17.
 */

public class VideoCompressorRuntimeProvider implements VideoCompressorRuntime {
    @Override
    public Promise<CompressedVideo> compressVideo(CompressVideoManager.CompressItem item, CompressorProgressListener progressCallback) {
        throw new RuntimeException("Dumb");
    }
}
