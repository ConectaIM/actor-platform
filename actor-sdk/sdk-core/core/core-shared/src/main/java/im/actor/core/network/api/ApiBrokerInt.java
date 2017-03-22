package im.actor.core.network.api;

import im.actor.core.api.parser.RpcParser;
import im.actor.core.api.parser.UpdatesParser;
import im.actor.core.network.ActorApiCallback;
import im.actor.core.network.AuthKeyStorage;
import im.actor.core.network.Endpoints;
import im.actor.core.network.parser.BaseParser;
import im.actor.runtime.actors.ActorInterface;
import im.actor.runtime.promise.Promise;

import static im.actor.runtime.actors.ActorSystem.system;

public class ApiBrokerInt extends ActorInterface {
    public ApiBrokerInt(final Endpoints endpoints, final AuthKeyStorage keyStorage, final ActorApiCallback callback,
                        final boolean isEnableLog, int id, final int minDelay,
                        final int maxDelay,
                        final int maxFailureCount,
                        BaseParser[] rpcParsers,
                        BaseParser[] updatesParsers) {
        setDest(system().actorOf("api/broker#" + id, () -> new ApiBroker(endpoints,
                keyStorage,
                callback,
                isEnableLog,
                minDelay,
                maxDelay,
                maxFailureCount,
                rpcParsers,
                updatesParsers)));
    }

    public Promise<Boolean> checkIsCurrentAuthId(long authId) {
        return ask(new CheckIsCurrentAuthId(authId));
    }


}
