package im.actor.core.modules.messaging.history;

import im.actor.core.api.ApiDocsHistoryType;
import im.actor.core.entity.Peer;
import im.actor.core.modules.ModuleContext;
import im.actor.runtime.actors.ActorInterface;
import im.actor.runtime.actors.messages.Void;
import im.actor.runtime.promise.Promise;

import static im.actor.runtime.actors.ActorSystem.system;

public class ConversationDocsHistory extends ActorInterface {

    public ConversationDocsHistory(Peer peer, ModuleContext context, ApiDocsHistoryType docType) {
        setDest(system().actorOf("historyDocs/"+docType+"/"+ peer, () -> {
            return new ConversationDocsHistoryActor(peer, context, docType);
        }));
    }

    public void loadMore() {
        send(new ConversationDocsHistoryActor.LoadMore());
    }

    public Promise<Void> reset() {
        return ask(new ConversationDocsHistoryActor.Reset());
    }
}
