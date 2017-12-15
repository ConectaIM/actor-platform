package im.actor.core.viewmodel;

import im.actor.core.entity.GroupPreState;
import im.actor.core.viewmodel.generics.BooleanValueModel;
import im.actor.runtime.mvvm.BaseValueModel;
import im.actor.runtime.mvvm.ValueModel;
import im.actor.runtime.mvvm.ValueModelCreator;

/**
 * Created by diego on 27/10/17.
 */

public class GroupPreVM extends BaseValueModel<GroupPreState> {

    public static ValueModelCreator<GroupPreState, GroupPreVM> CREATOR = new ValueModelCreator<GroupPreState, GroupPreVM>() {
        @Override
        public GroupPreVM create(GroupPreState baseValue) {
            return new GroupPreVM(baseValue);
        }
    };

    private ValueModel<Integer> parentId;
    private BooleanValueModel isLoaded;


    public GroupPreVM(GroupPreState rawObj) {
        super(rawObj);
        parentId = new ValueModel<Integer>("grupo_pre.parent_id." + rawObj.getGroupId(), rawObj.getParentId());
        isLoaded = new BooleanValueModel("grupo_pre.is_loaded." + rawObj.getGroupId(), rawObj.isLoaded());
    }

    @Override
    protected void updateValues(GroupPreState rawObj) {
        isLoaded.change(rawObj.isLoaded());
        parentId.change(rawObj.getParentId());
    }

    public BooleanValueModel getIsLoaded() {
        return isLoaded;
    }

    public ValueModel<Integer> getParentId() {
        return parentId;
    }
}