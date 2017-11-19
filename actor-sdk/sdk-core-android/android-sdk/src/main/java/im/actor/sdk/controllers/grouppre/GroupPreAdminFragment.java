package im.actor.sdk.controllers.grouppre;

import android.os.Bundle;

import im.actor.core.viewmodel.GroupVM;
import im.actor.sdk.controllers.BaseFragment;

/**
 * Created by dsilv on 18/11/2017.
 */

public class GroupPreAdminFragment extends BaseFragment{

    private GroupVM groupVM;

    public static GroupPreAdminFragment create(int groupId) {
        Bundle bundle = new Bundle();
        bundle.putInt("groupId", groupId);
        GroupPreAdminFragment editFragment = new GroupPreAdminFragment();
        editFragment.setArguments(bundle);
        return editFragment;
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

    }
}
