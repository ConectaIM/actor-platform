package im.actor.core.viewmodel;

import im.actor.core.entity.GrupoPreState;
import im.actor.core.viewmodel.generics.BooleanValueModel;
import im.actor.runtime.mvvm.BaseValueModel;
import im.actor.runtime.mvvm.ValueModelCreator;

/**
 * Created by diego on 27/10/17.
 */

public class GrupoPreVM extends BaseValueModel<GrupoPreState> {

    public static ValueModelCreator<GrupoPreState, GrupoPreVM> CREATOR = new ValueModelCreator<GrupoPreState, GrupoPreVM>() {
        @Override
        public GrupoPreVM create(GrupoPreState baseValue) {
            return new GrupoPreVM(baseValue);
        }
    };

    private BooleanValueModel isLoaded;
    private BooleanValueModel isEmpty;

    public GrupoPreVM(GrupoPreState rawObj) {
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