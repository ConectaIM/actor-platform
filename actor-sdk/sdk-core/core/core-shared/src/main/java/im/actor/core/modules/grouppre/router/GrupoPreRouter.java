package im.actor.core.modules.grouppre.router;

import java.util.ArrayList;
import java.util.List;


import im.actor.core.entity.GroupType;
import im.actor.core.entity.GrupoPre;
import im.actor.core.modules.ModuleActor;
import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.grouppre.router.entity.RouterApplyGruposPre;
import im.actor.core.network.parser.Update;
import im.actor.runtime.Log;
import im.actor.runtime.actors.messages.Void;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.storage.ListEngine;

public class GrupoPreRouter extends ModuleActor {

    private static final String TAG = GrupoPreRouter.class.getName();

    public GrupoPreRouter(ModuleContext context) {
        super(context);
    }


    private Promise<Void> onUpdate(Update update) {

        return Promise.success(null);
    }


    private Promise<Void> onGruposPreLoaded(Integer idGrupoPai, List<GrupoPre> grupos) {

        Log.d(TAG, "History Docs Loaded");

        updateGruposCanais(idGrupoPai, grupos);


        return Promise.success(null);
    }


    private ListEngine<GrupoPre> gruposPre(Integer idGrupoPai) {
        return ( context()).getGrupoPreModule().getGrupospreEngine(idGrupoPai);
    }

    private ListEngine<GrupoPre> canaisPre(Integer idGrupoPai) {
        return (context()).getGrupoPreModule().getCanaispreEngine(idGrupoPai);
    }

    private void updateGruposCanais(Integer idGrupoPai, List<GrupoPre> gruposPre) {
        List<GrupoPre> grupos = new ArrayList<>();
        List<GrupoPre> canais = new ArrayList<>();

        for (GrupoPre gp : gruposPre) {
            if (gp.getGroup().getGroupType().compareTo(GroupType.GROUP) == 0) {
                grupos.add(gp);
            } else if (gp.getGroup().getGroupType().compareTo(GroupType.CHANNEL) == 0) {
                canais.add(gp);
            }
        }

        gruposPre(idGrupoPai).addOrUpdateItems(grupos);
        canaisPre(idGrupoPai).addOrUpdateItems(canais);
    }

    @Override
    public Promise onAsk(Object message) throws Exception {
        if (message instanceof RouterApplyGruposPre) {
            RouterApplyGruposPre routerApplyGruposPre = (RouterApplyGruposPre) message;
            return onGruposPreLoaded(routerApplyGruposPre.getIdGrupoPai(), routerApplyGruposPre.getGruposPre());
        } else {
            return super.onAsk(message);
        }
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
    }
}
