package im.actor.core.modules.grouppre;

import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiGroup;
import im.actor.core.api.ApiGroupOutPeer;
import im.actor.core.api.ApiGroupPre;
import im.actor.core.api.rpc.RequestLoadGroups;
import im.actor.core.api.rpc.RequestLoadGroupsPre;
import im.actor.core.api.rpc.ResponseLoadGroupsPre;
import im.actor.core.entity.Group;
import im.actor.core.entity.GroupPre;
import im.actor.core.modules.ModuleActor;
import im.actor.core.modules.ModuleContext;
import im.actor.runtime.actors.messages.Void;
import im.actor.runtime.function.Function;
import im.actor.runtime.function.Tuple2;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.promise.Promises;

/**
 * Created by diego on 31/05/17.
 */

public class GrupoPreActor extends ModuleActor {

    private static final String KEY_VERSION = "_1";
    private final String KEY_LOADED;
    private final String KEY_LOADED_INIT;

    private Integer idGrupoPai;
    private boolean isLoading = false;
    private boolean isLoaded = false;


    public GrupoPreActor(ModuleContext context, Integer idGrupoPai) {
        super(context);
        this.idGrupoPai = idGrupoPai;
        KEY_LOADED = "grupo_pre_loaded_loaded" + "_" + idGrupoPai + KEY_VERSION;
        KEY_LOADED_INIT = "grupo_pre_inited" + "_" + idGrupoPai + KEY_VERSION;
    }

    @Override
    public void preStart() {
        isLoaded = preferences().getBool(KEY_LOADED, false);
        if (!preferences().getBool(KEY_LOADED_INIT, false)) {
            self().send(new GrupoPreActor.LoadGruposPre());
        } else {
            context().getConductor().getConductor().onGruposPreLoaded(null);
        }
    }

    private void onLoadGruposPre() {
        if (isLoading || isLoaded) {
            return;
        }

        isLoading = true;

        api(new RequestLoadGroupsPre(this.idGrupoPai))
                .map((ResponseLoadGroupsPre r) -> {
                    List<ApiGroupOutPeer> groupsOutPeer = new ArrayList<>();
                    for (ApiGroupPre groupPre : r.getGroups()) {
                        groupsOutPeer.add(new ApiGroupOutPeer(groupPre.getGroupId(), groupPre.getAcessHash()));
                    }
                    return new Tuple2<>(r.getGroups(), groupsOutPeer);
                })
                .flatMap(new Function<Tuple2<List<ApiGroupPre>, List<ApiGroupOutPeer>>, Promise<Tuple2<List<ApiGroupPre>, List<ApiGroup>>>>() {
                    @Override
                    public Promise<Tuple2<List<ApiGroupPre>, List<ApiGroup>>> apply(Tuple2<List<ApiGroupPre>, List<ApiGroupOutPeer>> r) {
                        return Promises.tuple(Promise.success(r.getT1()), api(new RequestLoadGroups(r.getT2())).map(r2 -> r2.getGroups()));
                    }
                })
                .map(new Function<Tuple2<List<ApiGroupPre>, List<ApiGroup>>, List<GroupPre>>() {
                    @Override
                    public List<GroupPre> apply(Tuple2<List<ApiGroupPre>, List<ApiGroup>> r) {
                        List<GroupPre> retorno = new ArrayList<GroupPre>();
                            for( ApiGroup apiGroup: ((List<ApiGroup>)r.getT2()) ){
                                for(ApiGroupPre apiGroupPre : (List<ApiGroupPre>) r.getT1()){
                                    if (apiGroup.getId() == apiGroupPre.getGroupId()) {
                                        retorno.add(new GroupPre(new Group(apiGroup, null), apiGroupPre.getOrder(), apiGroupPre.hasChildrem()));
                                    }
                                }

                            }
                        return retorno;
                    }
                })
                .map(result -> onGruposPreLoaded(result))
                .map(r -> {
                    isLoading = false;
                    return null;
                });
    }

    private Promise<Void> onGruposPreLoaded(List<GroupPre> groupsPre) {
        return context().getGrupoPreModule().getRouter()
                .onGruposPreLoaded(idGrupoPai, groupsPre)
                .map(r -> {
                    isLoaded = true;
                    preferences().putBool(KEY_LOADED, isLoaded);
                    preferences().putBool(KEY_LOADED_INIT, true);
                    return r;
                });
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof LoadGruposPre) {
            onLoadGruposPre();
        }
    }

    public static class LoadGruposPre {
        public LoadGruposPre() {
        }
    }

}
