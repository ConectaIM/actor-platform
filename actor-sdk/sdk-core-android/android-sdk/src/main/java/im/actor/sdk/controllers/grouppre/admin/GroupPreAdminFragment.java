package im.actor.sdk.controllers.grouppre.admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_pre_admin, null);



        return v;
    }
}
