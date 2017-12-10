package im.actor.core.modules.grouppre.router;

import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiGroup;
import im.actor.core.api.ApiGroupOutPeer;
import im.actor.core.api.ApiGroupPre;
import im.actor.core.api.rpc.RequestLoadGroups;
import im.actor.core.api.updates.UpdateGroupPreCreated;
import im.actor.core.entity.Group;
import im.actor.core.entity.GroupType;
import im.actor.core.entity.GroupPre;
import im.actor.core.modules.ModuleActor;
import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.grouppre.router.entity.RouterApplyGruposPre;
import im.actor.core.modules.grouppre.router.entity.RouterGroupPreUpdate;
import im.actor.core.network.parser.Update;
import im.actor.runtime.Log;
import im.actor.runtime.actors.messages.Void;
import im.actor.runtime.function.Consumer;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.storage.ListEngine;

public class GrupoPreRouter extends ModuleActor {

    private static final String TAG = GrupoPreRouter.class.getName();
    private boolean isFreezed = false;

    public GrupoPreRouter(ModuleContext context) {
        super(context);
    }

    private Promise<Void> onUpdate(Update update) {

        if(update instanceof UpdateGroupPreCreated){
            UpdateGroupPreCreated upd = (UpdateGroupPreCreated) update;

            final ApiGroupPre apiGroupPre = upd.getGroupPre();

            groups().containsAsync(apiGroupPre.getGroupId()).then(new Consumer<Boolean>() {
                @Override
                public void apply(Boolean contains) {
                        if(contains){
                            groups().getValueAsync(apiGroupPre.getGroupId()).then(new Consumer<Group>() {
                                @Override
                                public void apply(Group group) {
                                    GroupPre groupPre = new GroupPre(group, apiGroupPre.getOrder(), apiGroupPre.hasChildrem());
                                    if(group.getGroupType() == GroupType.GROUP){
                                        gruposPre(-1).addOrUpdateItem(groupPre);
                                    }else {
                                        canaisPre(-1).addOrUpdateItem(groupPre);
                                    }
                                }
                            });
                        }else{
                            List<ApiGroupOutPeer> groupsOutPeer = new ArrayList<>();
                            groupsOutPeer.add(new ApiGroupOutPeer(apiGroupPre.getGroupId(), apiGroupPre.getAcessHash()));

                            api(new RequestLoadGroups(groupsOutPeer)).then(r -> {
                                for(ApiGroup apiGroup : r.getGroups()){
                                    Group group = new Group(apiGroup, null);
                                    GroupPre groupPre = new GroupPre(group, apiGroupPre.getOrder(), apiGroupPre.hasChildrem());
                                    if(group.getGroupType() == GroupType.GROUP){
                                        gruposPre(-1).addOrUpdateItem(groupPre);
                                    }else {
                                        canaisPre(-1).addOrUpdateItem(groupPre);
                                    }
                                }
                            });
                        }
                }
            });

        }

        return Promise.success(null);
    }

    private void freeze() {
        isFreezed = true;
    }


    private void unfreeze() {
        isFreezed = false;
        unstashAll();
    }

    private Promise<Void> onGruposPreLoaded(Integer idGrupoPai, List<GroupPre> grupos) {
        Log.d(TAG, "History Docs Loaded");
        updateGruposCanais(idGrupoPai, grupos);
        return Promise.success(null);
    }


    private ListEngine<GroupPre> gruposPre(Integer idGrupoPai) {
        return context().getGrupoPreModule().getGrupospreEngine(idGrupoPai);
    }

    private ListEngine<GroupPre> canaisPre(Integer idGrupoPai) {
        return context().getGrupoPreModule().getCanaispreEngine(idGrupoPai);
    }

    private void updateGruposCanais(Integer idGrupoPai, List<GroupPre> gruposPre) {
        List<GroupPre> grupos = new ArrayList<>();
        List<GroupPre> canais = new ArrayList<>();

        for (GroupPre gp : gruposPre) {
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
        } else if(message instanceof RouterGroupPreUpdate) {
            if (isFreezed) {
                stash();
                return null;
            }
            return onUpdate(((RouterGroupPreUpdate) message).getUpdate());
        }else {
            return super.onAsk(message);
        }
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
    }
}
