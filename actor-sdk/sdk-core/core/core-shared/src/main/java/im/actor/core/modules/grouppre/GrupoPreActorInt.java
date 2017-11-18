package im.actor.core.modules.grouppre;

import im.actor.core.modules.ModuleContext;
import im.actor.runtime.actors.ActorInterface;

import static im.actor.runtime.actors.ActorSystem.system;

/**
 * Created by diego on 25/10/17.
 */

public class GrupoPreActorInt extends ActorInterface {

    public GrupoPreActorInt(Integer idGrupoPai, ModuleContext context) {
        system().actorOf("actor/grupopre/" + idGrupoPai, () -> new GrupoPreActor(context, idGrupoPai));
    }

    public void load() {
        send(new GrupoPreActor.LoadGruposPre());
    }

}
