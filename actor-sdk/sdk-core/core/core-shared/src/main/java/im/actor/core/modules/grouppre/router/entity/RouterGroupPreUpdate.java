package im.actor.core.modules.grouppre.router.entity;

import im.actor.core.network.parser.Update;
import im.actor.runtime.actors.ask.AskMessage;
import im.actor.runtime.actors.messages.Void;

/**
 * Created by diego on 23/11/2017.
 */

public class RouterGroupPreUpdate implements AskMessage<Void> {

    private Update update;

    public RouterGroupPreUpdate(Update update) {
        this.update = update;
    }

    public Update getUpdate() {
        return update;
    }
}
