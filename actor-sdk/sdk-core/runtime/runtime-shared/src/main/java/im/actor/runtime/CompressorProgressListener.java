package im.actor.runtime;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by diego on 23/05/17.
 */

public interface CompressorProgressListener {

    @ObjectiveCName("onProgress:withValue:")
    void onProgress(long rid, float value);

}
