package im.actor.sdk;

import android.app.Application;

import im.actor.core.AndroidMessenger;
import im.actor.core.ConfigurationBuilder;

/**
 * Created by diego on 26/05/17.
 */

public interface ActorSDKCreateListener {

    ActorSDKCreateListener stub = new ActorSDKCreateListener() {
        @Override
        public void onCreateActor(Application application) {
        }

        @Override
        public AndroidMessenger createMessenger(Application application, ConfigurationBuilder builder) {
            return new AndroidMessenger(application, builder.build());
        }
    };

    void onCreateActor(final Application application);

    AndroidMessenger createMessenger(Application application, ConfigurationBuilder builder);
}
