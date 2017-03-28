package im.actor.core;

import im.actor.core.api.updates.UpdateRawUpdate;
import im.actor.runtime.promise.Promise;

public abstract class RawUpdatesHandler {

    public abstract Promise<im.actor.runtime.actors.messages.Void> onRawUpdate(UpdateRawUpdate update);

}
