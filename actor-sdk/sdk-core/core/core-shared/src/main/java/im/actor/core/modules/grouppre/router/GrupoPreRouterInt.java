package im.actor.core.modules.grouppre.router;

import java.util.List;


import im.actor.core.entity.GrupoPre;
import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.grouppre.router.entity.RouterApplyGruposPre;
import im.actor.runtime.actors.ActorInterface;
import im.actor.runtime.actors.messages.Void;
import im.actor.runtime.promise.Promise;

import static im.actor.runtime.actors.ActorSystem.system;

public class GrupoPreRouterInt extends ActorInterface {

    public GrupoPreRouterInt(ModuleContext context) {
        setDest(system().actorOf("grupopre/router", () -> new GrupoPreRouter(context)));
    }

    public Promise<Void> onGruposPreLoaded(Integer idGrupoPai, List<GrupoPre> grupos) {
        return ask(new RouterApplyGruposPre(idGrupoPai, grupos));
    }

}
