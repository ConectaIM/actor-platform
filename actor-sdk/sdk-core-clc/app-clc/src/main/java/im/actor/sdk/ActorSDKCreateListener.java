package im.actor.sdk;

import im.actor.core.ConfigurationBuilder;

/**
 * Created by diego on 26/05/17.
 */

public interface ActorSDKCreateListener {

    ActorSDKCreateListener stub = new ActorSDKCreateListener() {
        @Override
        public void onCreateActor() {
        }

        @Override
        public ClcMessenger createMessenger(ConfigurationBuilder builder, String context) {
            return new ClcMessenger(builder.build(), context);
        }
    };

    void onCreateActor();

    ClcMessenger createMessenger(ConfigurationBuilder builder, String context);
}
