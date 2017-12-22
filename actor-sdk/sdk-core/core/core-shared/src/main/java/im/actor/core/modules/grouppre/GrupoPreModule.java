package im.actor.core.modules.grouppre;

import java.util.HashMap;

import im.actor.core.api.rpc.RequestChangeGroupPre;
import im.actor.core.entity.GroupPre;
import im.actor.core.entity.GroupPreState;
import im.actor.core.events.AppVisibleChanged;
import im.actor.core.modules.AbsModule;
import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.grouppre.router.GrupoPreRouterInt;
import im.actor.core.viewmodel.GroupPreVM;
import im.actor.runtime.Storage;
import im.actor.runtime.actors.messages.Void;
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

    private final HashMap<Integer, ListEngine<GroupPre>> gruposPreEngine = new HashMap<>();

    private MVVMCollection<GroupPreState, GroupPreVM> groupsPreStates;

    private final HashMap<Integer, GrupoPreActorInt> gruposPreLoadActor = new HashMap<>();
    private final GrupoPreRouterInt router;

    public GrupoPreModule(ModuleContext context) {
        super(context);
        router = new GrupoPreRouterInt(context);

        this.groupsPreStates = Storage.createKeyValue(STORAGE_GRUPOSPRE_STATES,
                GroupPreVM.CREATOR,
                GroupPreState.CREATOR,
                GroupPreState.DEFAULT_CREATOR);
    }

    @Override
    public void onBusEvent(Event event) {
        if (event instanceof AppVisibleChanged) {
            if(((AppVisibleChanged)event).isVisible())
                getGruposPreLoadActor(GroupPre.DEFAULT_ID);
        }
    }

    public void run() {
        context().getEvents().subscribe(this, AppVisibleChanged.EVENT);
    }

    public Promise<Void> changeGroupPre(int groupId, boolean isGroupPre) {
       return api(new RequestChangeGroupPre(groupId, isGroupPre))
               .flatMap(r -> updates().waitForUpdate(r.getSeq()));
    }

    public ListEngine<GroupPre> getGrupospreEngine(Integer idGrupoPai) {
        synchronized (gruposPreEngine) {
            if (!gruposPreEngine.containsKey(idGrupoPai)) {
                gruposPreEngine.put(idGrupoPai,
                        Storage.createList(STORAGE_GRUPOSPRE + idGrupoPai, GroupPre.CREATOR));
            }
            return gruposPreEngine.get(idGrupoPai);
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

    public MVVMCollection<GroupPreState, GroupPreVM> getGroupsPreStates() {
        return groupsPreStates;
    }

    public GroupPreVM getGrupoPreVM(Long idGrupoPre) {
        return groupsPreStates.get(idGrupoPre);
    }

}
