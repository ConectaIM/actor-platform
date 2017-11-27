package im.actor.sdk.controllers.grouppre;

import android.os.Bundle;

import im.actor.core.entity.GroupType;
import im.actor.core.viewmodel.GroupVM;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

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

    public GroupPreAdminFragment() {
        setRootFragment(true);
        setHomeAsUp(true);
        setShowHome(true);
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        groupVM = messenger().getGroup(getArguments().getInt("groupId"));
        setTitle(groupVM.getGroupType() == GroupType.CHANNEL ? R.string.channel_pre_admin_title : R.string.group_pre_admin_title);


    }
}
