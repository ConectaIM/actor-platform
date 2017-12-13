package im.actor.core.viewmodel;

import im.actor.core.entity.GroupPre;
import im.actor.core.entity.GroupPreState;
import im.actor.core.viewmodel.generics.BooleanValueModel;
import im.actor.runtime.mvvm.BaseValueModel;
import im.actor.runtime.mvvm.ValueModelCreator;

/**
 * Created by diego on 27/10/17.
 */

public class GrupoPreVM extends BaseValueModel<GroupPreState> {

    public static ValueModelCreator<GroupPre, GrupoPreVM> CREATOR = new ValueModelCreator<GroupPre, GrupoPreVM>() {
        @Override
        public GrupoPreVM create(GroupPre baseValue) {
            return new GrupoPreVM(GroupPre);
        }
    };

    private BooleanValueModel isLoaded;
    private BooleanValueModel isEmpty;

    public GrupoPreVM(GroupPre rawObj) {
        super(rawObj);
        isLoaded = new BooleanValueModel("grupo_pre.is_loaded." + rawObj.getIdGrupo(), rawObj.isLoaded());
        isEmpty = new BooleanValueModel("grupo_pre.is_empty." + rawObj.getIdGrupo(), rawObj.isEmpty());
    }

    public BooleanValueModel getIsLoaded() {
        return isLoaded;
    }

    public BooleanValueModel getIsEmpty() {
        return isEmpty;
    }


    @Override
    protected void updateValues(GrupoPreState rawObj) {
        isLoaded.change(rawObj.isLoaded());
        isEmpty.change(rawObj.isEmpty());
    }
}