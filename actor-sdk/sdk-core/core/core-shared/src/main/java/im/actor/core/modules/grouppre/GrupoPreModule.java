package im.actor.core.modules.grouppre;

import java.util.HashMap;



import im.actor.core.api.ApiGroupType;
import im.actor.core.api.rpc.RequestCreateGroupPre;
import im.actor.core.entity.GrupoPre;
import im.actor.core.entity.GrupoPreState;
import im.actor.core.events.AppVisibleChanged;
import im.actor.core.modules.AbsModule;
import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.grouppre.router.GrupoPreRouterInt;
import im.actor.core.util.RandomUtils;
import im.actor.core.viewmodel.GrupoPreVM;
import im.actor.runtime.Storage;
import im.actor.runtime.eventbus.BusSubscriber;
import im.actor.runtime.eventbus.Event;
import im.actor.runtime.mvvm.MVVMCollection;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.storage.ListEngine;

/**
 * Created by diego on 27/05/17.
 */

public class GrupoPreModule extends AbsModule implements BusSubscriber {

    public static final String STORAGE_GRUPOSPRE = "grupospre";
    public static final String STORAGE_GRUPOSPRE_STATES = "grupospre_states";

    private final HashMap<Integer, ListEngine<GrupoPre>> gruposPreEngine = new HashMap<>();
    private final HashMap<Integer, ListEngine<GrupoPre>> canaisPreEngine = new HashMap<>();

    private MVVMCollection<GrupoPreState, GrupoPreVM> gruposPreStates;

    private final HashMap<Integer, GrupoPreActorInt> gruposPreLoadActor = new HashMap<>();
    private final GrupoPreRouterInt router;


    public GrupoPreModule(ModuleContext context) {
        super(context);
        router = new GrupoPreRouterInt(context);
        this.gruposPreStates = Storage.createKeyValue(STORAGE_GRUPOSPRE_STATES,
                GrupoPreVM.CREATOR,
                GrupoPreState.CREATOR,
                GrupoPreState.DEFAULT_CREATOR);
    }

    @Override
    public void onBusEvent(Event event) {
        if (event instanceof AppVisibleChanged) {
            getGruposPreLoadActor(null);
        }
    }

    public void run() {
        context().getEvents().subscribe(this, AppVisibleChanged.EVENT);
    }

    public Promise<Integer> createGroupPre(int groupId, Integer parentId) {
       return api(new RequestCreateGroupPre(groupId, parentId))
                .chain(r -> updates().waitForUpdate(r.getSeq()))
                .map(r -> r.getGroupPre().getGroupId());
    }

    public ListEngine<GrupoPre> getGrupospreEngine(Integer idGrupoPai) {
        synchronized (gruposPreEngine) {
            if (!gruposPreEngine.containsKey(idGrupoPai)) {
                gruposPreEngine.put(idGrupoPai,
                        Storage.createList(STORAGE_GRUPOSPRE + ApiGroupType.GROUP + idGrupoPai, GrupoPre.CREATOR));
            }
            return gruposPreEngine.get(idGrupoPai);
        }
    }

    public ListEngine<GrupoPre> getCanaispreEngine(Integer idGrupoPai) {
        synchronized (canaisPreEngine) {
            if (!canaisPreEngine.containsKey(idGrupoPai)) {
                canaisPreEngine.put(idGrupoPai,
                        Storage.createList(STORAGE_GRUPOSPRE + ApiGroupType.CHANNEL + idGrupoPai, GrupoPre.CREATOR));
            }
            return canaisPreEngine.get(idGrupoPai);
        }
    }

    public GrupoPreRouterInt getRouter() {
        return router;
    }

    public GrupoPreActorInt getGruposPreLoadActor(final Integer idGrupoPai) {
        synchronized (gruposPreLoadActor) {
            if (!gruposPreLoadActor.containsKey(idGrupoPai)) {
                gruposPreLoadActor.put(idGrupoPai, new GrupoPreActorInt(idGrupoPai, context()));
            }
            return gruposPreLoadActor.get(idGrupoPai);
        }
    }

    public MVVMCollection<GrupoPreState, GrupoPreVM> getGruposPreStates() {
        return gruposPreStates;
    }

    public GrupoPreVM getGrupoPreVM(Long idGrupoPre) {
        return gruposPreStates.get(idGrupoPre);
    }

}
