package im.actor.core.modules.grouppre;

import im.actor.core.api.updates.UpdateGroupPreCreated;
import im.actor.core.api.updates.UpdateGroupPreParentChanged;
import im.actor.core.api.updates.UpdateGroupPreRemoved;
import im.actor.core.modules.AbsModule;
import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.sequence.processor.SequenceProcessor;
import im.actor.core.network.parser.Update;
import im.actor.runtime.actors.messages.Void;
import im.actor.runtime.promise.Promise;

/**
 * Created by diego on 23/11/2017.
 */

public class GroupPreProcessor extends AbsModule implements SequenceProcessor {
    public GroupPreProcessor(ModuleContext context) {
        super(context);
    }

    @Override
    public Promise<Void> process(Update update) {
        if (update instanceof UpdateGroupPreCreated ||
                update instanceof UpdateGroupPreRemoved ||
                update instanceof UpdateGroupPreParentChanged) {
            return context().getGrupoPreModule().getRouter().onUpdate(update);
        }
        return null;
    }
}
